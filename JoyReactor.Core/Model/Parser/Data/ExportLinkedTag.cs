namespace JoyReactor.Core.Model.Parser.Data
{
	public class ExportLinkedTag
	{
		public const int TYPE_POST_TAG = 1;
		public const int TYPE_INTEREST = 2;

		public string name { get; set; }
		public int type { get; set; }
		public string group { get; set; }
		public string image { get; set; }
		public string value { get; set; }
	}
}