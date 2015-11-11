using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Text.RegularExpressions;
using System.Threading;

public class Program {

    public static void Main() {
        
        //File.AppendAllText("log.txt", "count = " + new PageLoader().Get(null).Take(4).Count() + "\n");
        //if (true) return;
    
        foreach (var page in new PageLoader().Get(PageStorage.GetLastStopPage())) {
            File.AppendAllText("log.txt", "page = " + page.Url + "\n");
       
            var records = Regex
                .Matches(page.Document, "<div class=\"user\">(.+?)<div class=\"stars\"", RegexOptions.Multiline)
                .OfType<Match>()
                .Select(s => s.Groups[1].Value)
                .Select(s => new { 
                    name = Regex.Match(s, "href=\"/user/[^\"]+\">(.+?)</a>").Groups[1].Value.Trim(), 
                    icon = Regex.Match(s, "<img src=\"[^\"]+/user/(\\d+)\"").Groups[1].Value 
                });

            File.AppendAllLines("records.txt", records.SelectMany(s => new [] { s.name, s.icon, "================" }));
            PageStorage.SetLastPage(page.Url);  
            
            Thread.Sleep(1000);
        }
    }

    static string Decode(Match s) {
        return System.Net.WebUtility.HtmlDecode(s.Groups[2].Value.ToLower()).Replace("\"", "\\\"");
    }
    
    class PageStorage {
    
        public static string GetLastStopPage() {
            return File.Exists("index.txt") ? File.ReadAllText("index.txt") : null;
        }
        
        public static void SetLastPage(string page) {
            if (File.Exists("index.txt")) File.Delete("index.txt");
            File.WriteAllText("index.txt", page);
        }
    }
    
    class PageLoader {
    
        Page lastPage;
    
        public IEnumerable<Page> Get(string prevFinishPage) {
            while (true) {
                var nextPageUrl = GetNextPage(prevFinishPage);
                
                File.AppendAllText("log.txt", "nextPageUrl = " + nextPageUrl + "\n");
                
                if (nextPageUrl == null) yield break;
                yield return lastPage = new Page { 
                    Url = nextPageUrl, 
                    Document = new HttpClient().GetStringAsync(nextPageUrl).Result 
                };
            }
        }

        string GetNextPage(string prevFinishPage) {
            if (lastPage == null) {
                if (prevFinishPage == null) return "http://joyreactor.cc/people";
                lastPage = new Page { 
                    Url = prevFinishPage, 
                    Document = new HttpClient().GetStringAsync(prevFinishPage).Result 
                };
            }
            
            var match = Regex.Match(lastPage.Document, "<a href=[\"']([^\"']+/\\d+)[\"'] +class=[\"']next[\"']>");

            File.AppendAllText("log.txt", "match = " + match + "\n");
           
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