using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using Android.Views;
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

        public void Destroy()
        {
            bindings.Clear();
        }
    }
}