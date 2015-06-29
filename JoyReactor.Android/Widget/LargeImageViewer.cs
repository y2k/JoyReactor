using System;
using Android.Content;
using Android.Graphics;
using Android.Util;
using Android.Views;
using JoyReactor.Android.Model;

namespace JoyReactor.Android.Widget
{
    public class LargeImageViewer : View
    {
        DynamicImage image;
        TouchDetector touchDetector;

        public LargeImageViewer(Context context, IAttributeSet attrs)
            : base(context, attrs)
        {
            SetImage(null);
            touchDetector = new TouchDetector.Router
            {
                TransateDetector = new TouchDetector.Translate((x, y) => image.Translate(x, y)),
                ScaleDetector = new TouchDetector.Zoom(this, (scale, x, y) => image.Zoom(scale, x, y)),
            };
        }

        public void SetImage(string pathToImage)
        {
            image = pathToImage == null ? DynamicImage.Stub : new DynamicImage(this, pathToImage);
            RequestLayout();
        }

        protected override void OnDraw(Canvas canvas)
        {
            canvas.Save();
            canvas.Scale(image.GetCurrentFrameScale(), image.GetCurrentFrameScale());
            canvas.DrawBitmap(image.GetCurrentFrame(), 0, 0, new Paint { FilterBitmap = true });
            canvas.Restore();
        }

        public override bool OnTouchEvent(MotionEvent e)
        {
            if (touchDetector.HandlerTouchEvent(e))
                return true;
            return base.OnTouchEvent(e);
        }

        protected override void OnLayout(bool changed, int left, int top, int right, int bottom)
        {
            base.OnLayout(changed, left, top, right, bottom);
            image.Layout(right - left, bottom - top);
        }

        abstract class TouchDetector : Java.Lang.Object
        {
            public abstract bool HandlerTouchEvent(MotionEvent e);

            internal class Router : TouchDetector
            {
                internal TouchDetector TransateDetector { get; set; }

                internal TouchDetector ScaleDetector { get; set; }

                bool blockUntilDown;

                public override bool HandlerTouchEvent(MotionEvent e)
                {
                    if (blockUntilDown && e.Action != MotionEventActions.Down)
                        return false;
                    blockUntilDown = false;
                    if (e.Action == MotionEventActions.PointerUp)
                    {
                        blockUntilDown = true;
                        return false;
                    }

                    return e.PointerCount == 1
                        ? TransateDetector.HandlerTouchEvent(e)
                        : ScaleDetector.HandlerTouchEvent(e);
                }
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

            internal class Zoom : TouchDetector, ScaleGestureDetector.IOnScaleGestureListener
            {
                ScaleGestureDetector detector;

                Action<float, float, float> callback;

                View parent;

                internal Zoom(View parent, Action<float, float, float> callback)
                {
                    this.parent = parent;
                    this.callback = callback;
                    detector = new ScaleGestureDetector(parent.Context, this);
                }

                public override bool HandlerTouchEvent(MotionEvent e)
                {
                    return detector.OnTouchEvent(e);
                }

                public bool OnScale(ScaleGestureDetector detector)
                {
                    callback(detector.ScaleFactor,
                        detector.FocusX / parent.Width,
                        detector.FocusY / parent.Height);
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