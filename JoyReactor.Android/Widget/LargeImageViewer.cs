using System;
using System.Threading.Tasks;
using Android.Content;
using Android.Graphics;
using Android.Util;
using Android.Views;

namespace JoyReactor.Android.Widget
{
    public class LargeImageViewer : View
    {
        MetaImage image;
        ToggleButton button;
        TouchDetector touchDetector;

        public LargeImageViewer(Context context, IAttributeSet attrs)
            : base(context, attrs)
        {
            button = new ToggleButton();
            button.OnToggle += (sender, e) =>
            {
                touchDetector = button.IsZoom 
                        ? (TouchDetector)new TouchDetector.Zoom(image.Zoom) 
                        : new TouchDetector.Translate(image.Translate);
            };
            touchDetector = new TouchDetector.Translate((x, y) => image.Translate(x, y));
            SetImage(null);
        }

        public void SetImage(string pathToImage)
        {
            image = pathToImage == null ? MetaImage.Stub : new MetaImage(this, pathToImage);
        }

        protected override void OnDraw(Canvas canvas)
        {
            canvas.DrawBitmap(image.GetCurrentFrame(), 0, 0, new Paint { FilterBitmap = true });
            button.Draw(canvas);
        }

        public override bool OnTouchEvent(MotionEvent e)
        {
            if (button.HandlerTouchEvent(e))
                return true;
            if (touchDetector.HandlerTouchEvent(e))
                return true;
            return base.OnTouchEvent(e);
        }

        protected override void OnLayout(bool changed, int left, int top, int right, int bottom)
        {
            base.OnLayout(changed, left, top, right, bottom);
            image.Layout(right - left, bottom - top);
        }

        class MetaImage
        {
            public static readonly MetaImage Stub = new MetaImage(null, null);

            BitmapRegionDecoder decoder;
            Rect destRect;
            Rect srcRect;

            Task activeTask;

            Bitmap bufferFrame;
            Bitmap currentFrame;

            View parent;

            public MetaImage(View parent, string pathToImage)
            {
                this.parent = parent;
                decoder = pathToImage == null ? null 
                    : BitmapRegionDecoder.NewInstance(pathToImage, false);
            }

            public Bitmap GetCurrentFrame()
            {
                // TODO:
                return currentFrame ?? Bitmap.CreateBitmap(4, 4, Bitmap.Config.Argb8888);
            }

            public void Layout(int width, int height)
            {
                if (decoder == null)
                    return;
                if (destRect == null)
                {
                    destRect = new Rect(0, 0, width, height);
                    srcRect = new Rect(0, 0, width, height);
                }
                Invalidate();
            }

            public void Zoom(float x, float y)
            {
                throw new NotImplementedException();
            }

            public void Translate(float x, float y)
            {
                srcRect.Offset((int)(-x), (int)(-y));
                Invalidate();
            }

            async void Invalidate()
            {
                if (activeTask == null)
                {
                    activeTask = Task.Run(
                        () =>
                        {
                            var o = new BitmapFactory.Options();
                            o.InSampleSize = 1;
                            o.InBitmap = bufferFrame;
                            currentFrame = decoder.DecodeRegion(srcRect, o);
                        });
                    await activeTask;

                    var t = currentFrame;
                    currentFrame = bufferFrame;
                    bufferFrame = t;

                    activeTask = null;
                    parent.Invalidate();
                }
            }
        }

        class ToggleButton
        {
            public bool IsZoom { get; private set; }

            public event EventHandler OnToggle;

            public void Draw(Canvas canvas)
            {
                // TODO:
            }

            public bool HandlerTouchEvent(MotionEvent e)
            {
                // TODO:
                return false;
            }
        }

        abstract class TouchDetector
        {
            public virtual bool HandlerTouchEvent(MotionEvent e)
            {
                throw new NotImplementedException();
            }

            internal class Translate : TouchDetector
            {
                PointF lastCoords;
                Action<float, float> callback;

                internal Translate(Action<float, float> callback)
                {
                    this.callback = callback;
                }

                public override bool HandlerTouchEvent(MotionEvent e)
                {
                    switch (e.Action)
                    {
                        case MotionEventActions.Down:
                            lastCoords = new PointF(e.GetX(), e.GetY());
                            return true;
                        case MotionEventActions.Move:
                            if (lastCoords == null)
                                return false;
                            callback(e.GetX() - lastCoords.X, e.GetY() - lastCoords.Y);
                            lastCoords = new PointF(e.GetX(), e.GetY());
                            return true;
                        case MotionEventActions.Up:
                        case MotionEventActions.Cancel:
                            if (lastCoords == null)
                                return false;
                            lastCoords = null;
                            return true;
                    }
                    return false;
                }
            }

            internal class Zoom : TouchDetector
            {
                internal Zoom(Action<float, float> callback)
                {
                    // TODO:
                }
            }
        }
    }
}