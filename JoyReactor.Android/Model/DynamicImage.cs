using System;
using System.Threading.Tasks;
using Android.Graphics;
using Android.Views;
using IllegalArgumentException = Java.Lang.IllegalArgumentException;

namespace JoyReactor.Android.Model
{
    public class DynamicImage
    {
        public static readonly DynamicImage Stub = new DynamicImage(null, null);

        BitmapRegionDecoder decoder;
        PointF frameSize;

        Bitmap bufferFrame;
        Bitmap currentFrame;

        View parent;
        bool isBusy;
        Matrix sceneMatrix;
        float currentFrameScale;

        public DynamicImage(View parent, string pathToImage)
        {
            this.parent = parent;
            decoder = pathToImage == null ? null 
                : BitmapRegionDecoder.NewInstance(pathToImage, false);
        }

        public float GetCurrentFrameScale()
        {
            return currentFrameScale;
        }

        public Bitmap GetCurrentFrame()
        {
            return currentFrame ?? Bitmap.CreateBitmap(4, 4, Bitmap.Config.Argb8888);
        }

        public void Layout(int width, int height)
        {
            if (frameSize == null)
            {
                frameSize = new PointF(width, height);

                bufferFrame = Bitmap.CreateBitmap((int)frameSize.X, (int)frameSize.Y, Bitmap.Config.Argb8888);
                currentFrame = Bitmap.CreateBitmap((int)frameSize.X, (int)frameSize.Y, Bitmap.Config.Argb8888);

                Invalidate();
            }
        }

        public void Scale(float scale, float centerX, float centerY)
        {
            if (decoder == null)
                return;

            sceneMatrix.PreScale(1 / scale, 1 / scale, centerX * frameSize.X, centerY * frameSize.Y);
            Invalidate();
        }

        public void Translate(float x, float y)
        {
            if (decoder == null)
                return;

            sceneMatrix.PreTranslate(-x, -y);
            Invalidate();
        }

        async void Invalidate()
        {
            if (isBusy || decoder == null)
                return;

            isBusy = true;
            await ReloadImagePart();
            isBusy = false;
            parent.Invalidate();
        }

        async Task ReloadImagePart()
        {
            if (sceneMatrix == null)
                SetInitMatrix();

            var decodeRect = new RectF { Right = frameSize.X, Bottom = frameSize.Y };
            sceneMatrix.MapRect(decodeRect);
            var sceneScale = Math.Min(frameSize.X / decodeRect.Width(), frameSize.Y / decodeRect.Height());

            var o = new BitmapFactory.Options();
            o.InSampleSize = Math.Max(1, (int)Math.Pow(2, Math.Ceiling(Math.Log(1 / sceneScale, 2))));
            o.InBitmap = bufferFrame;

            bufferFrame = await Task.Run(
                () =>
                {
                    new Canvas(bufferFrame).DrawColor(Color.Black);
                    try
                    {
                        var outRect = new Rect();
                        decodeRect.Round(outRect);
                        return decoder.DecodeRegion(outRect, o);
                    }
                    catch (IllegalArgumentException)
                    {
                        // Игнорируем выход за пределы картинки
                        return bufferFrame;
                    }
                });

            currentFrameScale = sceneScale * o.InSampleSize;
            SwapBuffers();
        }

        void SetInitMatrix()
        {
            sceneMatrix = new Matrix();
            var scale = Math.Min(frameSize.X / decoder.Width, frameSize.Y / decoder.Height);
            Scale(scale, 0, 0);
            Translate(
                0.5f * (frameSize.X - decoder.Width * scale), 
                0.5f * (frameSize.Y - decoder.Height * scale));
        }

        void SwapBuffers()
        {
            var temp = currentFrame;
            currentFrame = bufferFrame;
            bufferFrame = temp;
        }
    }
}