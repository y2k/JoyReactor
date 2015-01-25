namespace JoyReactor.Core.Model.DTO
{
    public class CommentWithChildCount : Comment
    {
        public int ChildCount { get; set; }

        public string[] Images { get; set; }
    }
}