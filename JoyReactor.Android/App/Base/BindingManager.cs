using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Reflection;
using Android.Views;
using Android.Widget;
using GalaSoft.MvvmLight.Helpers;

namespace JoyReactor.Android.App.Base
{
    public class BindingManager
    {
        List<Binding> bindings = new List<Binding>();

        public Binding<TS, TT> Add<TS, TT>(object source, Expression<Func<TS>> sourceExpression, object target, Expression<Func<TT>> targetExpression, BindingMode mode = BindingMode.Default)
        {
            var binding = source.SetBinding(sourceExpression, target, targetExpression, mode);
            bindings.Add(binding);
            return binding;
        }

        public Binding<T, T> Add<T>(object source, Expression<Func<T>> sourceExpression, BindingMode mode = BindingMode.Default)
        {
            var binding = source.SetBinding(sourceExpression, mode);
            bindings.Add(binding);
            return binding;
        }

        public Binding<bool, bool> Add(object source, Expression<Func<bool>> sourceExpression, View target)
        {
            var func = sourceExpression.Compile();
            return Add(source, sourceExpression)
                .WhenSourceChanges(() => target.Visibility = func() ? ViewStates.Visible : ViewStates.Gone);
        }

        public Binding Add(object source, Expression<Func<string>> sourceExpression, EditText target)
        {
            var prop = (PropertyInfo)((MemberExpression)sourceExpression.Body).Member;
            var binding = Add(source, sourceExpression)
                .WhenSourceChanges(() =>
                {
                    var text = (string)prop.GetValue(source);
                    if (target.Text != text)
                        target.Text = text;
                });
            target.TextChanged += (sender, e) => prop.SetValue(source, target.Text);
            return binding;
        }

        public Binding Add<T>(object source, Expression<Func<T>> sourceExpression, TextView target)
        {
            var prop = (PropertyInfo)((MemberExpression)sourceExpression.Body).Member;
            return Add(source, sourceExpression)
                .WhenSourceChanges(() => target.Text = "" + prop.GetValue(source));
        }

        public void Destroy()
        {
            bindings.Clear();
        }
    }
}