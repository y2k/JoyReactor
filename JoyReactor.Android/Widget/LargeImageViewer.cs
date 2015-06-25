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
            button = new ToggleButton(this);
            button.OnToggle += (sender, e) =>
            {
                touchDetector = button.IsZoom 
                        ? (TouchDetector)new TouchDetector.Zoom(this, image.Zoom)
                        : new TouchDetector.Translate(image.Translate);
                Invalidate();
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
                    Invalidate();
                }
            }

            public void Zoom(float scale)
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

            Rect rect;
            bool pressed;

            float Radius;

            public ToggleButton(View parent)
            {
                Radius = parent.Resources.DisplayMetrics.Density * 48;
            }

            public void Draw(Canvas canvas)
            {
                rect = new Rect(canvas.Width - (int)Radius, 0, canvas.Width, (int)Radius);
                canvas.DrawRect(rect, new Paint { Color = Color.Red });
                canvas.DrawText(IsZoom ? "Z" : "T", rect.Left, rect.Bottom, new Paint{ TextSize = Radius, Color = Color.White });
            }

            public bool HandlerTouchEvent(MotionEvent e)
            {
                if (!rect.Contains((int)e.GetX(), (int)e.GetY()))
                    return false;
                switch (e.Action)
                {
                    case MotionEventActions.Down:
                        pressed = true;
                        return true;
                    case MotionEventActions.Up:
                    case MotionEventActions.Cancel:
                        if (!pressed)
                            return false;
                        pressed = false;
                        IsZoom = !IsZoom;
                        OnToggle(this, null);
                        return true;
                }
                return false;
            }
        }

        abstract class TouchDetector : Java.Lang.Object
        {
            public abstract bool HandlerTouchEvent(MotionEvent e);

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

            internal class Zoom : TouchDetector, ScaleGestureDetector.IOnScaleGestureListener
            {
                ScaleGestureDetector detector;

                Action<float> callback;

                internal Zoom(View parent, Action<float> callback)
                {
                    this.callback = callback;
                    detector = new ScaleGestureDetector(parent.Context, this);
                }

                public override bool HandlerTouchEvent(MotionEvent e)
                {
                    return detector.OnTouchEvent(e);
                }

                public bool OnScale(ScaleGestureDetector detector)
                {
                    callback(detector.ScaleFactor);
                    return true;
                }

                public bool OnScaleBegin(ScaleGestureDetector detector)
                {
                    return true;
                }

                public void OnScaleEnd(ScaleGestureDetector detector)
                {
                }
            }
        }
    }
}