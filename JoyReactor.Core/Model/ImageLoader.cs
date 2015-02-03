//using Microsoft.Practices.ServiceLocation;
//using System;
//using System.Threading.Tasks;
//using System.Threading;
//
//namespace JoyReactor.Core.Model
//{
//    public class ImageLoader
//    {
//        ImageModel model = ServiceLocator.Current.GetInstance<ImageModel>();
//
//        Uri source;
//        int maxSize;
//
//        public ImageLoader(Uri source, int maxSize)
//        {
//            this.source = source;
//            this.maxSize = maxSize;
//        }
//
//        public Task<object> Load()
//        {
//            return Load<object>();
//        }
//
//        public Task<T> Load<T>()
//        {
//            object result = null;
//            var asyncResult = new AsyncResult();
//            var task = new TaskFactory().FromAsync(asyncResult, s => (T)result);
//
//            model.Load(new object(), source, maxSize, s =>
//            {
//                result = s;
//                asyncResult.IsCompleted = true;
//                ((EventWaitHandle)asyncResult.AsyncWaitHandle).Set();
//            });
//
//            return task;
//        }
//
//        class AsyncResult : IAsyncResult
//        {
//            public object AsyncState { get { return null; } }
//
//            public WaitHandle AsyncWaitHandle { get; } = new ManualResetEvent(false);
//
//            public bool CompletedSynchronously { get { return false; } }
//
//            public bool IsCompleted { get; set; }
//        }
//    }
//}