using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;

public class Program {

    public static void Main() {
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
    
    static MatchCollection MatchUsers(string page) {
        return Regex.Matches(page, "<div class=\"user\">(.+?)<div class=\"stars\"", RegexOptions.Multiline);
    }

    static string ClearName(string s) {
        return WebUtility.HtmlDecode(s.Trim());
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