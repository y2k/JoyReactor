using GalaSoft.MvvmLight;
using JoyReactor.Core.Model;
using JoyReactor.Core.Model.DTO;
using JoyReactor.Core.Model.Inject;
using Microsoft.Practices.ServiceLocation;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace JoyReactor.WP.ViewModel
{
    public class AttachmentsViewModel : BaseViewModel
    {
        public ObservableCollection<CommentAttachment> Attachments { get; set; }
        
        private IPostModel model = ServiceLocator.Current.GetInstance<IPostModel>();

        public AttachmentsViewModel()
        {
            Attachments = new ObservableCollection<CommentAttachment>();
        }

        public async override void InitializeWithBundle(IDictionary<string, string> state)
        {
            Attachments.Clear();
            var at = await model.GetAttachmentsAsync(int.Parse(state["id"]));
            at.ForEach(s => Attachments.Add(s));
        }
    }
}