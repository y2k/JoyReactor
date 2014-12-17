using System;
using System.Collections.Generic;

namespace JoyReactor.Core.Model.Web.Parser.Data
{
    public class ExportPostInformation
    {
        public ExportAttachment[] Attachments { get; set; } = new ExportAttachment[0];

        public ExportUser User { get; set; }

        public string Title { get; set; }

        public DateTime Created { get; set; }

        public float Rating { get; set; }

        public string Content { get; set; }
    }
}