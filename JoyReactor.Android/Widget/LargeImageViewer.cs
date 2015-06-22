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
            SetImage(null);
        }

        public void SetImage(string pathToImage)
        {
            image = pathToImage == null ? MetaImage.Stub : new MetaImage(pathToImage);
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
            public static readonly MetaImage Stub = new MetaImage(null);

            BitmapRegionDecoder decoder;
            Rect destRect;
            Rect srcRect;

            Task activeTask;

            public MetaImage(string pathToImage)
            {
                decoder = pathToImage == null ? null 
                    : BitmapRegionDecoder.NewInstance(pathToImage, false);
            }

            public Bitmap GetCurrentFrame()
            {
                // TODO:
                return Bitmap.CreateBitmap(4, 4, Bitmap.Config.Argb8888);
            }

            public void Layout(int width, int height)
            {
                if (destRect == null)
                    destRect = new Rect(0, 0, width, height);
            }

            public void Zoom(float x, float y)
            {
                throw new NotImplementedException();
            }

            public void Translate(float x, float y)
            {
                throw new NotImplementedException();
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
            public bool HandlerTouchEvent(MotionEvent e)
            {
                throw new NotImplementedException();
            }

            internal class Translate : TouchDetector
            {
                internal Translate(Action<float, float> callback)
                {
                    // TODO:
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