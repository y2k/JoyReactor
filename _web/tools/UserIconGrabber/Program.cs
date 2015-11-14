using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

public class Program {

    public static void Main() {
        if (!File.Exists("records.txt")) {
            var lines = new PageLoader().Get()
                .SelectMany(page => MatchUsers(page.Document).OfType<Match>())
                .Select(s => s.Groups[1].Value)
                .Select(s => new { 
                    name = ClearName(Regex.Match(s, "href=\"/user/[^\"]+\">(.+?)</a>").Groups[1].Value),
                    icon = Regex.Match(s, "<img src=\"[^\"]+/user/(\\d+)\"").Groups[1].Value 
                })
                .Where(s => !string.IsNullOrEmpty(s.icon))
                .Select(s => s.icon + " " + s.name);
            File.WriteAllLines("records.txt", lines);
        }
        
        File.ReadAllLines("records.txt")
            .Select(s => s.Split(new char[] { ' ' }, 2))
            .Select(s => new { icon = s[0], name = s[1] })
            .GroupBy(s => HashCode(s.name))
            .Where(s => s.Count() > 1)
            .Select(s => s.Key + " | " + s.Select(a => a.name).Aggregate((a, b) => a + ", " + b))
            //.Select(s => s.icon + " | " + s.name)
            .Take(100).ToList().ForEach(s => Console.WriteLine(s));
    }
    
    static MatchCollection MatchUsers(string page) {
        return Regex.Matches(page, "<div class=\"user\">(.+?)<div class=\"stars\"", RegexOptions.Multiline);
    }

    static string ClearName(string s) {
        return WebUtility.HtmlDecode(s.Trim());
    }
        
    static int HashCode(string str) {
        char[] val = str.ToCharArray();
        int h = 0;
        unchecked {
            for (int i = 0; i < str.Length; i++) {
                h = 31 * h + val[i];
            }
        }
        return h;
    }
        
    class PageLoader {
    
        Page lastPage;
    
        public IEnumerable<Page> Get() {
            while (true)
            {
                var nextPageUrl = GetNextPage();

                if (nextPageUrl == null) yield break;
                yield return lastPage = new Page
                {
                    Url = nextPageUrl,
                    Document = Download(nextPageUrl).Result
                };
            }
        }

        private async Task<string> Download(string url)
        {
            Console.WriteLine("Load page: " + url);
            for (int i = 0 ; i < 6 ; i++) {
                try { 
                    await Task.Delay(1000 << i);
                    return await new HttpClient().GetStringAsync(url);
                } catch (Exception e) {
                    Console.WriteLine("Error: " + e);
                }
            }
            throw new Exception("Can't download " + url);
        }

        string GetNextPage() {
            if (lastPage == null) return "http://joyreactor.cc/people/top";

            var match = Regex.Match(lastPage.Document, "<a href=[\"']([^\"']+/\\d+)[\"'] +class=[\"']next[\"']>");
            if (match.Success)
                return "" + new Uri(new Uri(lastPage.Url), match.Groups[1].Value);
            return null;
        }
    }
    
    class Page {
        
        public string Document;
        public string Url;
    }
}