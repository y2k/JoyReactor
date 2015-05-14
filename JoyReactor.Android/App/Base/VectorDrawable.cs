using Android.Graphics.Drawables;
using Android.Graphics;

namespace JoyReactor.Android.App.Base
{
    public class VectorDrawable : Drawable
    {
        string name;

        VectorDrawable(string name)
        {
            this.name = name;
        }

        public override void Draw(Canvas canvas)
        {
            canvas.Save();
            canvas.Scale(GetScale(), GetScale());

            var width = canvas.Width / GetScale();
            var height = canvas.Height / GetScale();

            var path = new Path();
            var paint = new Paint { AntiAlias = true };
            if (name.StartsWith("inbox"))
            {
                paint.Color = Color.ParseColor("#ffa726");
                if (name.EndsWith("first"))
                {
                    path.MoveTo(0, 0);
                    path.LineTo(12, 14);
                    path.LineTo(24, 0);
                    path.LineTo(0, 0);
                }
                path.AddRoundRect(12, 0, canvas.Width / GetScale(), canvas.Height / GetScale(), 3, 3, Path.Direction.Ccw);
            }
            else
            {
                paint.Color = Color.White;
                if (name.EndsWith("first"))
                {
                    path.MoveTo(canvas.Width / GetScale(), 0);
                    path.LineTo(canvas.Width / GetScale() - 12, 14);
                    path.LineTo(canvas.Width / GetScale() - 24, 0);
                    path.LineTo(canvas.Width / GetScale(), 0);
                }
                path.AddRoundRect(0, 0, canvas.Width / GetScale() - 12, canvas.Height / GetScale(), 3, 3, Path.Direction.Cw);
            }
            canvas.DrawPath(path, paint);

            if (!name.StartsWith("inbox"))
            {
                paint.Color = Color.ParseColor("#E0E0E0");
                paint.SetStyle(Paint.Style.Stroke);
                paint.StrokeWidth = 1;

                if (name.EndsWith("first"))
                {
                    path = new Path();
                    path.AddArc(0, 0, 6, 6, 180, 90);
                    path.LineTo(width, 0);
                    path.LineTo(width - 12, 14);
                    path.LineTo(width - 12, height - 3);
                    path.AddArc(width - 12 - 6, height - 6, width - 12, height, 0, 90);
                    path.LineTo(3, height);
                    path.AddArc(0, height - 6, 6, height, 90, 90);
                    path.LineTo(0, 3);
                }
            }
            canvas.DrawPath(path, paint);

            canvas.Restore();
        }

        static float GetScale()
        {
            return App.Instance.Resources.DisplayMetrics.Density;
        }

        public override void SetAlpha(int alpha)
        {
        }

        public override void SetColorFilter(ColorFilter cf)
        {
        }

        public override int Opacity { get { return 0; } }

        public static VectorDrawable NewVectorDrawable(string name)
        {
            return new VectorDrawable(name);
        }
    }
}