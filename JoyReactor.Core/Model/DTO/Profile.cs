using System;
using SQLite.Net.Attributes;

namespace JoyReactor.Core.Model.DTO
{
	[Table("profiles")]
	public class Profile
	{
		[PrimaryKey]
		public string Site { get; set; }

		public string Username { get; set; }

		public string Cookie { get; set; }
	}
}