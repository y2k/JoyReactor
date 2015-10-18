using System.IO;
using System.Linq;
using System.Net;
using System.Text.RegularExpressions;
using System.Web;

public class Program
{
    public static void Main()
    {
        var tags = Enumerable
            .Range(1, 1)
            .Select(s => s == 1 ? "" : "/" + s)
            .Select(s => new WebClient().DownloadString("http://joyreactor.cc/tags/subscribers" + s))
            .Select(s => Regex.Matches(s, "<img src=\"[^\"]+/tag/(\\d+)\" alt=\"([^\"]+)\"/>"))
            .SelectMany(s => s.OfType<Match>())
            .Select(s => new { key = Decode(s), value = s.Groups[1].Value})
            .Distinct()
            .OrderBy(s => s.key)
            .ToList();

        File.WriteAllLines("bin/tags.keys.txt", tags.Select(s => "\"" + s.key + "\","));
        File.WriteAllLines("bin/tags.values.txt", tags.Select(s => "\"" + s.value + "\","));
    }

    static string Decode(Match s)
    {
        return HttpUtility.HtmlDecode(s.Groups[2].Value.ToLower()).Replace("\"", "\\\"");
    }
}