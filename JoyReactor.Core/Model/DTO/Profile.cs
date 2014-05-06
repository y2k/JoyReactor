using Cirrious.MvvmCross.Community.Plugins.Sqlite;
using System;

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