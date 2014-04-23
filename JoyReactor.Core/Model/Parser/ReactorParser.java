package net.itwister.joyreactor.models.parsers;

import android.net.Uri;

import net.itwister.joyreactor.models.common.Ln;
import net.itwister.joyreactor.models.common.Proc;
import net.itwister.joyreactor.models.common.XpathUtils;
import net.itwister.joyreactor.models.common.utils.PatternUtils;
import net.itwister.joyreactor.models.data.Attachment;
import net.itwister.joyreactor.models.data.Comment;
import net.itwister.joyreactor.models.data.ID;
import net.itwister.joyreactor.models.data.LinkedTag;
import net.itwister.joyreactor.models.data.Post;
import net.itwister.joyreactor.models.data.PreviewPost;
import net.itwister.joyreactor.models.data.Profile;
import net.itwister.joyreactor.models.web.Downloader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bindui.InjectService;

public class ReactorParser implements SiteParser {

	private static final Pattern RATING = Pattern.compile("Рейтинг:\\s*<div class=\"[^\"]+\"></div>\\s*([\\d\\.]+)");
	private static final Pattern POST = Pattern.compile("<div id=\"postContainer\\d+\" class=\"postContainer\">(.*?)<div class=\"vote-minus unregistered\">", Pattern.DOTALL);
    private static final Pattern POST_AUTHORIZED = Pattern.compile("<div id=\"postContainer\\d+\" class=\"postContainer\">(.*?)<div class=\"vote-minus", Pattern.DOTALL);

	private static final Pattern IMAGE = Pattern.compile("<div class=\"image\">\\s*<img src=\"([^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)", Pattern.DOTALL);
    private static final Pattern IMAGE_BIG = Pattern.compile("<div class=\"image\">\\s*<a href=\"([^\"]+)\" class=\"prettyPhotoLink\" rel=\"prettyPhoto\">\\s*<img src=\"[^\"]+\" width=\"(\\d+)\" height=\"(\\d+)\"", Pattern.DOTALL);
    private static final Pattern IMAGE_IN_POST = Pattern.compile("<img src=\"([^\"]+/pics/post/[^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)", Pattern.DOTALL);
    private static final Pattern IMAGE_GIF = Pattern.compile("ссылка на гифку</a><img src=\"([^\"]+)\" width=\"(\\d+)\" height=\"(\\d+)");

	private static final Pattern USER_NAME = Pattern.compile("href=\"[^\"]+user/([^\"/]+)\"", Pattern.DOTALL);
	private static final Pattern USER_IMAGE = Pattern.compile("src=\"([^\"]+)\" class=\"avatar\"");
	private static final Pattern TITLE = Pattern.compile("<div class=\"post_content\"><span>([^<]*)</span>", Pattern.DOTALL);
	private static final Pattern POST_ID = Pattern.compile("<a href=\"/post/(\\d+)\"", Pattern.DOTALL);
	private static final Pattern CREATED = Pattern.compile("data\\-time=\"(\\d+)\"");

	private static final Pattern USER_ID = Pattern.compile("userId=\"(\\d+)\"");

	private static final String COMMENT_START = "<div class=\"post_comment_list\">";

	private static final Pattern TEXT = Pattern.compile("comment_txt_\\d+_\\d+\">\\s*<span>(.*?)</span>", Pattern.DOTALL);

	private static final Pattern COMMENT_ID = Pattern.compile("comment_txt_\\d+_(\\d+)");
	private static final Pattern TIMESTAMP = Pattern.compile("timestamp=\"(\\d+)");

	private static final Pattern COMMENT_IMAGES = Pattern.compile("<img src=\"(http://[^\"]+/pics/comment/)[^\"]+(\\-\\d+\\.[^\"]+)");

	private static final Pattern TAGS = Pattern.compile("<a title=\"(.+?)\" data\\-ids=");
	private static final Pattern TAGS_INTEREST = Pattern.compile(">([^\\(<>]+)\\(\\d+\\)</");

	private static final Pattern SUB_POSTER = Pattern.compile("src=\"([^\"]+)\" *alt=\"[^\"]+\" *class=\"blog_avatar\" */>");

	private static final Pattern SIMILAR_POST = Pattern.compile("<td class=\"similar_post\">(.+?)</td>", Pattern.DOTALL);
	private static final Pattern SIMILAR_POST_ID = Pattern.compile("<a href=\"/post/(\\d+)\">");
	private static final Pattern SIMILAR_POST_IMAGE = Pattern.compile("<img src=\"([^\"]+)");

	private static final Pattern SUB_LINKED_SUBS = Pattern.compile("<img src=\"(http://img\\d+.joyreactor\\.cc/pics/avatar/tag/\\d+)\"\\s+alt=\"([^\"]+)\"\\s*/>\\s*</td>\\s*<td>\\s*<a href=\"[^\"]+tag/([^\"]+)\"");

	private static final Pattern COUB = Pattern.compile("<iframe src=\"http://coub.com/embed/(.+?)\" allowfullscreen=\"true\" frameborder=\"0\" width=\"(\\d+)\" height=\"(\\d+)");

	private static final Pattern SIMILAR_POST_TITLE = Pattern.compile("<img src=\"[^\"]+\" alt=\"([^\"]+)");
	private static final Pattern SIMILAR_POST_TITLE2 = Pattern.compile("<a href=\"[^\"]*/tag/[^\"]+\">\\s*([^<]+)\\s*</a>");
	private static final Pattern SIMILAR_POST_TITLE3 = Pattern.compile("<a href=\"http://([\\w\\d]+)\\.joyreactor\\.cc/\">");

    private static final Pattern CURRENT_PAGE = Pattern.compile("<span class='current'>(\\d+)</span>");
    private static final Pattern sProfileRating = Pattern.compile("([\\d\\.]+)");

    private Downloader downloader = InjectService.getInstance(Downloader.class);
    private CookieHolder cookieHolder = InjectService.getInstance(CookieHolder.class);

	@Override
	public void extractPost(String id, final ExtractPostCallbacks callbacks) throws Exception {
        Map<String, String> cook = cookieHolder.get(CookieHolder.REACTOR);
        cook.put("showVideoGif2", "1");
		String html = downloader.get(generateUrl(id), null, cook);
        Document doc = Parser.parse(html, generateUrl(id));

		callbacks.onExtractBegin();

		{
			PostDetailsExport p = new PostDetailsExport();

			Matcher m = IMAGE_IN_POST.matcher(html);
			if (m.find()) {
				p.image = m.group(1);
				p.imageWidth = Integer.parseInt(m.group(2));
				p.imageHeight = Integer.parseInt(m.group(3));
			}
            if (p.image == null) {
                m = IMAGE_GIF.matcher(html);
                if (m.find()) {
                    p.image = m.group(1);
                    p.imageWidth = Integer.parseInt(m.group(2));
                    p.imageHeight = Integer.parseInt(m.group(3));
                }
            }
			if (p.image == null) {
				p.image = PatternUtils.group(Pattern.compile("\\[img\\]([^\\[]+)\\[/img\\]"), html);
				if (p.image != null) { // XXX Проверить нужно ли ставить фейковые размеры картинки и какое конкретно число
					p.imageWidth = 512;
					p.imageHeight = 512;
				}
			}
            if (p.image != null) p.image = p.image.replaceAll("/pics/post/.+-(\\d+\\.[\\d\\w]+)", "/pics/post/-$1");

			p.userName = Uri.decode(Uri.decode(PatternUtils.group(USER_NAME, html))).replace('+', ' ');
			p.userImage = PatternUtils.group(USER_IMAGE, html);

			p.title = PatternUtils.group(TITLE, html);
			if (p.title != null && p.title.isEmpty()) p.title = null;

			//			p.id = PatternUtils.group(POST_ID, html);

			p.created = PatternUtils.groupLong(CREATED, html) * 1000L;

			p.rating = PatternUtils.groupFloat(RATING, html);

			//		p.coub = PatternUtils.group(COUB, html);
			m = COUB.matcher(html);
			if (m.find()) {
				p.coub = m.group(1);
				p.imageWidth = Integer.parseInt(m.group(2));
				p.imageHeight = Integer.parseInt(m.group(3));
			}

			callbacks.onExtractPostDetails(p);
		}

		{
			int pos = html.indexOf(COMMENT_START) + COMMENT_START.length();
			pos = skipHtmlTag(html, pos);
			readChildComments(html, pos, null, new Proc<Comment>() {

				@Override
				public void run(Comment comment) {
					callbacks.onExtractComments(comment);
				}
			});
		}

		{ // TODO
			//			Matcher m = TAGS.matcher(html);
			//			while (m.find()) {
			//				LinkedTag tag = LinkedTag.create(m.group(1), LinkedTag.TYPE_POST_TAG);
			//				callbacks.onExtractTags(tag);
			//			}
			//
			//			m = TAGS_INTEREST.matcher(html);
			//			while (m.find()) {
			//				LinkedTag tag = LinkedTag.create(m.group(1), LinkedTag.TYPE_INTEREST);
			//				callbacks.onExtractTags(tag);
			//			}

//			Matcher m = SUB_LINKED_SUBS.matcher(html);
//			while (m.find()) {
//				LinkedTag t = new LinkedTag();
//				t.name = StringEscapeUtils.unescapeHtml4(m.group(2));
//				t.group = "None";
//				t.image = m.group(1);
//				t.value = Uri.decode(Uri.decode(m.group(3)));
//				callbacks.onExtractTags(t);
//			}

            for (Element g : doc.select("div.sidebar_block")) {
                String gt = XpathUtils.innerTextTrim(g, "h2.sideheader.random");
                if (gt != null) {
                    for (Element e : g.select("tr")) {
                        LinkedTag t = new LinkedTag();
                        t.name = XpathUtils.innerTextTrim(e, "a");
                        t.group = gt;
                        t.image = XpathUtils.firstUrl(e, "img", "src");
                        t.value = PatternUtils.group(Pattern.compile("/tag/(.+)"), XpathUtils.firstAttr(e, "a", "href"));
                        callbacks.onExtractTags(t);
                    }
                }
            }
		}

		{
			//
			Matcher m = SIMILAR_POST.matcher(html);
			while (m.find()) {
				String s = m.group(1);

				PreviewPost pp = new PreviewPost();
				pp.id = PatternUtils.group(SIMILAR_POST_ID, s);
				pp.image = PatternUtils.group(SIMILAR_POST_IMAGE, s);

				{
					String s1 = PatternUtils.aggregateGroups(SIMILAR_POST_TITLE2, s, ", ");
					s1 = s1 == null ? "" : s1.trim();
					String s2 = PatternUtils.aggregateGroups(SIMILAR_POST_TITLE3, s, ", ");
					s2 = s2 == null ? "" : s2.trim();

					if (s1.length() == 0 && s2.length() == 0) pp.title = null;
					else if (s1.length() == 0) pp.title = s2;
					else if (s2.length() == 0) pp.title = s1;
					else pp.title = s1 + ", " + s2;
				}
				if (pp.title == null) pp.title = PatternUtils.group(SIMILAR_POST_TITLE, s);

				callbacks.onExtractLinkedPosts(pp);
			}
		}
	}

	@Override
	public void extractPostCollection(ID id, int lastLoadedPage, ExtractPostCollectionCallbacks callback) throws Exception {
        Map<String, String> cook = cookieHolder.get(CookieHolder.REACTOR);
        cook.put("showVideoGif2", "1");

		String html = downloader.get(generateUrl(id, lastLoadedPage), null, cook);
		callback.onExtractBegin();

		SubscriptionExport s = new SubscriptionExport();
		s.image = PatternUtils.group(SUB_POSTER, html);
        s.firstPage = (int) PatternUtils.groupLong(CURRENT_PAGE, html);
		callback.onExtractSubscriptonInfo(s);

		Matcher m = POST.matcher(html);
        if (m.find()) {
            do {
                Post p = createPost(m.group(1));
                callback.onExtractPost(p);
            } while (m.find());
        } else {
            m = POST_AUTHORIZED.matcher(html);
            while (m.find()) {
                Post p = createPost(m.group(1));
                callback.onExtractPost(p);
            };
        }


		if (lastLoadedPage == 0) {
			m = SUB_LINKED_SUBS.matcher(html);
			while (m.find()) {
				LinkedTag t = new LinkedTag();
				t.name = StringEscapeUtils.unescapeHtml4(m.group(2));
				t.group = "None";
				t.image = m.group(1);
				t.value = Uri.decode(Uri.decode(m.group(3)));
				callback.onExtractLinkedSubscription(t);
			}
		}
	}

    @Override
    public Map<String, String> login(String username, String password) throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse r = client.execute(new HttpGet("http://joyreactor.cc/login"));
        Document doc = Jsoup.parse(EntityUtils.toString(r.getEntity()));
        String csrf = doc.select("#signin__csrf_token").get(0).attr("value");

        List<Cookie> cookies = client.getCookieStore().getCookies();
        Connection.Response resp = createConnection("http://joyreactor.cc/login", null, true)
                .method(Connection.Method.POST)
                .data("signin[username]", username)
                .data("signin[password]", password)
                .data("signin[remember]", "on")
                .data("signin[_csrf_token]", csrf)
                .data("submit", "Войти")
                .cookie(cookies.get(0).getName(), cookies.get(0).getValue())
                .followRedirects(false)
                .execute();

        Map<String, String> c = resp.cookies();
        if (!c.containsKey("joyreactor")) throw new Exception("Fail to login");
        return c;
//        cookieHolder.put(CookieHolder.REACTOR, c);
    }

    @Override
    public Profile profile(String username) throws Exception {
        String url = "http://joyreactor.cc/user/" + Uri.encode(username);
        Document doc = createConnection(url, null, true).get();

        Profile p = new Profile();
        p.username = username;

        Element div = doc.select("div.user > img").get(0);
        p.imageUrl = div.absUrl("src");

        div = doc.select("#rating-text > b").get(0);
        Matcher m = sProfileRating.matcher(div.text().replace(" ", ""));
        if (m.find()) p.rating = Float.parseFloat(m.group(1));
        else throw new RuntimeException("can't find rating (username = " + username + ")");

        return p;
    }

    // ==============================================================
	// Скрытые методы
	// ==============================================================

    public static Connection createConnection(String url, Map<String, String> cookies, boolean followRedirects) {
        Ln.v("createConnection(), url = " + url);
        Connection conn = Jsoup.connect(url)
                .userAgent("Opera/9.80 (X11; Linux x86_64; U; en) Presto/2.10.289 Version/12.02")
                .followRedirects(followRedirects)
                .referrer(url)
                .header("Accept", "text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1")
                .header("Accept-Language", "ru,en;q=0.9")
                .timeout(20000);
        if (cookies != null) conn.cookies(cookies);
        return conn;
    }

	private Post createPost(String html) {
		Post p = new Post();

		Matcher m = IMAGE.matcher(html);
		if (m.find()) {
			p.image = m.group(1);
			p.imageWidth = Integer.parseInt(m.group(2));
			p.imageHeight = Integer.parseInt(m.group(3));
		}
        if (p.image == null) {
            m = IMAGE_GIF.matcher(html);
            if (m.find()) {
                p.image = m.group(1);
                p.imageWidth = Integer.parseInt(m.group(2));
                p.imageHeight = Integer.parseInt(m.group(3));
            }
        }
        if (p.image == null) {
            m = IMAGE_BIG.matcher(html);
            if (m.find()) {
                p.image = m.group(1);
                p.imageWidth = Integer.parseInt(m.group(2));
                p.imageHeight = Integer.parseInt(m.group(3));
            }
        }
		if (p.image == null) {
			p.image = PatternUtils.group(Pattern.compile("\\[img\\]([^\\[]+)\\[/img\\]"), html);
			if (p.image != null) { // XXX Проверить нужно ли ставить фейковые размеры картинки и какое конкретно число
				p.imageWidth = 512;
				p.imageHeight = 512;
			}
		}
        if (p.image != null) {
            p.image = p.image.replaceAll("/pics/post/full/[\\w\\s%-]+-(\\d+\\.[\\d\\w]+)", "/pics/post/full/-$1");
            p.image = p.image.replaceAll("/pics/post/[\\w\\s%-]+-(\\d+\\.[\\d\\w]+)", "/pics/post/full/-$1");
        }

        p.userName = Uri.decode(Uri.decode(PatternUtils.group(USER_NAME, html))).replace('+', ' ');
		p.userImage = PatternUtils.group(USER_IMAGE, html);

		p.title = PatternUtils.group(TITLE, html);
		if (p.title != null && p.title.isEmpty()) p.title = null;

		p.id = PatternUtils.group(POST_ID, html);

		p.created = PatternUtils.groupLong(CREATED, html) * 1000L;

		p.rating = PatternUtils.groupFloat(RATING, html);

		//		p.coub = PatternUtils.group(COUB, html);
		m = COUB.matcher(html);
		if (m.find()) {
			p.coub = m.group(1);
			p.imageWidth = Integer.parseInt(m.group(2));
			p.imageHeight = Integer.parseInt(m.group(3));
		}

		return p;
	}

    private String generateUrl(ID id, int beforeLoadedPage) {
        StringBuilder url = new StringBuilder("http://joyreactor.cc");
        if (ID.TYPE_FAVORITE.equals(id.type)) {
            url.append("/user/").append(Uri.encode(id.tag)).append("/favorite");
        } else {
            if (id.tag != null) url.append("/tag/").append(Uri.encode(id.tag));
            if (ID.TYPE_BEST.equals(id.type)) url.append("/best");
            else if (ID.TYPE_ALL.equals(id.type)) url.append(id.tag == null ? "/all" : "/new");
        }

        if (beforeLoadedPage > 0) url.append("/").append(beforeLoadedPage - 1);
        if (url.toString().equals("http://joyreactor.cc")) url.append("/");
        return url.toString();
    }

//	private String generateUrl(ID id, int firstPage, int skipCount) {
//		StringBuilder url = new StringBuilder("http://joyreactor.cc");
//		if (id.tag != null) url.append("/tag/").append(Uri.encode(id.tag));
//
//		if (ID.TYPE_BEST.equals(id.type)) url.append("/best");
//		else if (ID.TYPE_ALL.equals(id.type)) url.append(id.tag == null ? "/all" : "/new");
//
//		if (page > 0) url.append("/").append(page + 1);
//		return url.toString();
//	}

	private String generateUrl(String id) {
		return String.format("http://joyreactor.cc/post/%s", id);
	}

	private Comment getComment(String html, int start, int end) {
		String s = html.substring(start, end + 1);
		Comment c = new Comment();

		c.id = PatternUtils.group(COMMENT_ID, s);
		c.text = PatternUtils.group(TEXT, s);
		c.created = PatternUtils.groupLong(TIMESTAMP, s) * 1000L;

		c.userName = Uri.decode(Uri.decode(PatternUtils.group(USER_NAME, s))).replace('+', ' ');
		c.userImage = "http://img0.joyreactor.cc/pics/avatar/user/" + PatternUtils.group(USER_ID, s);

		Matcher m = COMMENT_IMAGES.matcher(s);
		if (m.find()) {
			String u = m.group(1) + m.group(2);

			Attachment a = new Attachment();
			a.imageUrl = u;
			c.attachments = Collections.singletonList(a);
		}

		return c;
	}

	private int readChildComments(String html, int position, String parentId, Proc<Comment> callback) throws Exception {
		int end;
		int initPosition = position;
		while (true) {
			end = readTag(html, position);
			if (end < 0) break;

			// Оптимизированная (по памяти и CPU) проверка случая когда "нет комментариев"
			if (parentId == null && position == initPosition) {
				if (end - position < 100 && html.substring(position, end).contains("нет комментариев")) return position;
			}

			Comment c = getComment(html, position, end);
			c.parentId = parentId;
			callback.run(c);

			end = skipHtmlTag(html, end + 1);
			end = readChildComments(html, end, c.id, callback);
			position = skipHtmlTag(html, end);
		}
		return position;
	}

	private int readTag(String html, int position) {
		int level = 0;
		do {
			int i = html.indexOf('<', position);
			int endTag = html.indexOf('>', i + 1);

			if ("<br>".equals(html.substring(i, i + 4))) {
				position += 4;
				continue;
			} else if (html.charAt(endTag - 1) == '/') {
				position = endTag + 1;
				continue;
			}

			level += html.charAt(i + 1) == '/' ? -1 : 1;
			position = i + 1;
		} while (level > 0);
		return level < 0 ? -1 : html.indexOf('>', position);
	}

	private int skipHtmlTag(String html, int position) {
		return html.indexOf('>', position) + 1;
	}
}