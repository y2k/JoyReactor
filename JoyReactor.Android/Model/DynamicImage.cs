using System;
using System.Threading.Tasks;
using Android.Graphics;
using Android.Views;

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

        PointF sceneTranslate = new PointF();
        float sceneScale = 1;

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

        public void Zoom(float scale)
        {
            sceneScale *= scale;
            Invalidate();
        }

        public void Translate(float x, float y)
        {
            sceneTranslate.X -= x / sceneScale;
            sceneTranslate.Y -= y / sceneScale;
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
            var decodeRect = 
                new Rect
                {
                    Left = (int)sceneTranslate.X,
                    Top = (int)sceneTranslate.Y,
                    Right = (int)(sceneTranslate.X + frameSize.X / sceneScale),
                    Bottom = (int)(sceneTranslate.Y + frameSize.Y / sceneScale),
                };

            var o = new BitmapFactory.Options();
            o.InSampleSize = Math.Max(1, (int)Math.Pow(2, Math.Ceiling(Math.Log(1 / sceneScale, 2))));
            o.InBitmap = bufferFrame;

            bufferFrame = await Task.Run(
                () =>
                {
                    new Canvas(bufferFrame).DrawColor(Color.Black);
                    return decoder.DecodeRegion(decodeRect, o);
                });
            currentFrameScale = sceneScale * o.InSampleSize;
                 
            SwapBuffers();
        }

        void SwapBuffers()
        {
            var temp = currentFrame;
            currentFrame = bufferFrame;
            bufferFrame = temp;
        }
    }
}