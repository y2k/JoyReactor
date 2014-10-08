using System;

namespace JoyReactor.Core.Model.Web.Parser
{
	public struct ThreadId
	{
		public string Id { get; set; }

		public string Board { get; set; }

		public static ThreadId Unpack (string packedId)
		{
			var idParts = packedId.Split (',');
			return new ThreadId { Board = idParts [0], Id = idParts [1] };
		}

		public string Pack ()
		{
			return Board + "," + Id;
		}

		public override string ToString ()
		{
			return string.Format ("[ThreadId: Board={0}, Id={1}]", Board, Id);
		}
	}
}