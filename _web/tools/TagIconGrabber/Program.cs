using System;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Text.RegularExpressions;
using System.Threading;

public class Program
{
    public static void Main()
    {
        if (!File.Exists("temp-buffer.txt")) {
            var lines = Enumerable
                .Range(1, 400)
                .Select(s => s == 1 ? "" : "/" + s)
                .Select(s => {
                    Thread.Sleep(500);
                    return new HttpClient().GetStringAsync("http://joyreactor.cc/tags/subscribers" + s).Result;
                })
                .Select(s => Regex.Matches(s, "<img src=\"[^\"]+/tag/(\\d+)\" alt=\"([^\"]+)\"/>"))
                .SelectMany(s => s.OfType<Match>())
                .Select(s => new { name = Decode(s.Groups[2].Value), icon = s.Groups[1].Value})
                .Select(s => s.icon + " " + s.name);
            File.WriteAllLines("temp-buffer.txt", lines);
        }
                    
        var tags = File.ReadAllLines("temp-buffer.txt")
            .Select(s => s.Split(new char[] { ' ' }, 2))
            .Select(s => new { icon = int.Parse(s[0]), name = HashCode(s[1]) })
            .GroupBy(s => s.name)
            .Select(s => s.First())
            .OrderBy(s => s.name)
            .ToList();        
        File.WriteAllBytes("tag.names.dat", tags.SelectMany(s => BitConverter.GetBytes(s.name)).ToArray());
        File.WriteAllBytes("tag.icons.dat", tags.SelectMany(s => BitConverter.GetBytes(s.icon)).ToArray());
    }

    static string Decode(string s)
    {
        return System.Net.WebUtility.HtmlDecode(s.ToLower()).Replace("\"", "\\\"");
    }
    
    static int HashCode(string str) {
        char[] val = str.ToCharArray();
        int h = 0;
        for (int i = 0; i < str.Length; i++)
            h = 31 * h + val[i];
        return h;
    }
}