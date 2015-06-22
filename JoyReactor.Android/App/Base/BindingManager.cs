using System;
using GalaSoft.MvvmLight.Helpers;
using System.Linq.Expressions;
using System.Collections.Generic;

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

        public void Destroy()
        {
            bindings.Clear();
        }
    }
}