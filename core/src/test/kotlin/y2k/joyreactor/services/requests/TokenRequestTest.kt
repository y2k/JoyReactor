package y2k.joyreactor.services.requests

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import y2k.joyreactor.http.HttpClient

/**
 * Created by y2k on 4/26/16.
 */
class TokenRequestTest {

    val mockHttpClient = mock(HttpClient::class.java).apply {
        `when`(getText("http://joyreactor.cc/donate")).then { testHtml }
    }

    @Test
    fun test() {
        val actual = TokenRequest(mockHttpClient).request().toBlocking().first()
        assertEquals(actual, "f374b51bc0fe00c0f6e6159aa28fee3f")
    }
}

const private val testHtml = """<!doctype html>
    <!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="ru"> <![endif]-->
    <!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang="ru"> <![endif]-->
    <!--[if IE 8]>         <html class="no-js lt-ie9" lang="ru"> <![endif]-->
    <!--[if gt IE 8]><!--> <html class="no-js" lang="ru"> <!--<![endif]-->
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="title" content="JoyReactor - смешные картинки и другие приколы: комиксы, гиф анимация, видео, лучший интеллектуальный юмор." />
    <title>JoyReactor - смешные картинки и другие приколы: комиксы, гиф анимация, видео, лучший интеллектуальный юмор.</title>
    <link href='http://fonts.googleapis.com/css?family=Open+Sans:400,700&subset=latin,cyrillic-ext' rel='stylesheet' type='text/css'>


    <link rel="stylesheet" type="text/css" media="screen" href="http://izfbxg.jr-cdn.com/css/optimized.css?v=1458126278" />
    <script type="text/javascript" src="http://nluffz.jr-cdn.com/js/jquery.min.js"></script>
<script type="text/javascript" src="http://izfbxg.jr-cdn.com/v/JoG69p6ocLvkKIb-NHiqJdqSWQV9HA?1461571545"></script>



            <link rel="stylesheet" type="text/css" media="screen" href="/main/colors.css?_=2016-04-26+17%3A04"/>


            <style type="text/css">
            .article .ufoot div.comment .reply-link a.response {
                display: none;
            }
        </style>



    <link rel="apple-touch-icon" href="/images/apple-icon.png">
    <link rel="apple-touch-icon-precomposed" href="/images/apple-icon-precomposed.png">
    <link rel="shortcut icon" href="/favicon.ico"/>
	<meta name="google-site-verification" content="Z-5bKeVVN7wUlux89waW0C3Om7yp43Ish708yUnK86Y" />
            <link title="" type="application/rss+xml" rel="alternate" href="/rss"/>

    <script type="text/Javascript">
        var user_id = 0;
        var token = 'f374b51bc0fe00c0f6e6159aa28fee3f';
                                var moderated_tags = [];
        var server_time = 1461679477;
        var view_num = 3;
                    var update_lang = 1;
            </script>
    <script type='text/javascript' src="/main/localized.ru.js?v=11"></script>
            <script src="http://nluffz.jr-cdn.com/c/34967.js?_=1461679477"></script>
<script src="http://nluffz.jr-cdn.com/c/index.php?dt=15&s=34967&pl=Win32&sw=1920&jq=0&vi=1&ref=&uid=iqAGCQs&k=tYNiICF7mFAEtsusdgq2gasdIB8eSWZ7-_pyryPvxGs5b_dvyhpjCGHdezeeXQoqWAiwMiXrya9KdXR1WlhK23m.2HjaT.R.3QTF3nn&un=0&fl=1&sh=1080&ab=1&tt=JoyReactor%20-%20%D1%81%D0%BC%D0%B5%D1%88%D0%BD%D1%8B%D0%B5%20%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8%20%D0%B8%20%D0%B4%D1%80%D1%83%D0%B3%D0%B8%D0%B5%20%D0%BF%D1%80%D0%B8%D0%BA%D0%BE%D0%BB%D1%8B%3A%20%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B%2C%20%D0%B3%D0%B8%D1%84%20%D0%B0%D0%BD%D0%B8%D0%BC%D0%B0%D1%86%D0%B8%D1%8F%2C%20%D0%B2%D0%B8%D0%B4%D0%B5%D0%BE%2C%20%D0%BB%D1%83%D1%87%D1%88%D0%B8%D0%B9%20%D0%B8%D0%BD%D1%82%D0%B5%D0%BB%D0%BB%D0%B5%D0%BA%D1%82%D1%83%D0%B0%D0%BB%D1%8C%D0%BD%D1%8B%D0%B9%20%D1%8E%D0%BC%D0%BE%D1%80."></script>
<script language="JavaScript" type="text/javascript">
    if(typeof jQuery == "undefined" || typeof FixGifVideo == "undefined" || typeof jr_js_loaded == 'undefined' || !jr_js_loaded[1] || !jr_js_loaded[2]){
        document.write('Из-за блокировщика рекламы, сайт работает некорректно. Пожалуйста, отключите блокировщик на нашем сайте, или добавьте исключение <br/>@@||jr-cdn.com//${'$'}script');
        window.stop();
        document.execCommand('Stop');
    }
</script>    </head>
<body>
<div id="joytip"></div>
<div id="background">
    <div id="container">
        <div id="topbar" class="topbar_wr">
            <div class="topbar_inner">
                <a href="/" class="top_logo">JoyReactor</a>
                <div class="topbar_right">
                                                                                    <div class="swf_switcher">
                        <span>sfw</span>
                        <div class="sswither ">
                            <a class="submenu_locker" href="javascript:" title="Безопасно для работы (убирает эротику и всякое такое)">
                                <span></span>
                            </a>
                        </div>
                        <span>nsfw</span>
                    </div>
                                            <div class="lang_select">
                          <a href="http://joyreactor.com"><img src="/images/icon_en.png" alt="English version" title="English version" /></a>
                        </div>
                                        <ul class="login_wr">    <li class="login lastitem">
    <a href="/register">Регистрация</a></li>
<li class="login">
    <a href="/login">Вход</a></li>
</ul>
                </div>
            </div>
        </div>
        <div id="header">
                                        <div class="logo"><a href="/">JoyReactor: приколы, смешные картинки</a></div>
                    <div class="description">Лабильность и нонконформизм</div>

        </div>
                <div id="page">
            <div id="navcontainer">
                <ul id="navlist">
                    <li id="first">
                        <a href="#"> </a>
                    </li>
                    <li class=''>
    <span><a href="/">Лента</a></span>
</li>
<li class=''>
    <span><a href="/discussion">Обсуждаемое</a></span>
</li>
<li class=''>
    <span><a href="/people/top">Люди</a></span>
</li>
<!--<li >
    <span><a href="/memeSelect">Сделай сам</a></span>
</li>-->
<li id="about"  class="current_page_item">
    <span><a href="/about">О проекте</a></span>
</li>

                </ul>

                <div class="post_random">
                    <a href="/random">Случайный пост</a>                </div>

                <div id="searchmenu">
                    <form action="/search" method="GET" id="searchform">
    <input id="s" type="text" name="q" size="20" placeholder="Поиск" />
    <input id="searchsubmit" type="submit" value="Поиск" />
</form>                </div>
            </div>
            <div id="searchBar">
                <div id="submenu">
                        <div class="submenuitem "><a href="/about">О проекте</a></div>
    <div class="submenuitem "><a href="/ads">Реклама</a></div>
    <div class="submenuitem "><a href="/sitemap">Карта сайта</a></div>
            <div class="submenuitem "><a href="/chat">Чатик</a></div>
        <!-- <div class="submenuitem "><a href="/radio">Радио</a></div> -->
        <div class="submenuitem active"><a href="/donate">Поддержать проект</a></div>
                    </div>

            </div>
            <div id="pageinner">
                <div id="content">
                    <div id="contentinner">


<h3>Нравится реактор? Хотите ему помочь? Будем благодарны!</h3>

Можно сделать анонимное пожертвование, но для зарегистированных пользователей будут такие плюшки:<br/><br/>
1) при оплате от <b>100 руб</b> - полностью отключается реклама на сайте. Навсегда. <br/>Также добавится медалька мецената и будет виден статус аккаунта: в бане или нет, срок бана при наличии.<br/><br/>
2) при оплате от <b>200 руб</b> даем 1 звездочку, от 1000р - две звездочки. Со старыми звёздами не суммируются.<br/><br/>

Бонусы зачисляются в течение 24 часов после перевода. Пишите на <b>joy@joyreactor.com</b>, если будут проблемы или пожелания.<br/>


<p>
    </p>

<p>

</p>

<p>
    </p>
<p>
    </p>

<p>
    <p><a href="/login" class="inactive_button">Отключить рекламу (необходимо залогиниться)</a></p>
    <p><a href="/login" class="inactive_button">Получить 1 звездочку (необходимо залогиниться)</a></p>
    <p><a href="/login" class="inactive_button">Получить 2 звездочки (необходимо залогиниться)</a></p>
</p>
<p>

    <form id="payment" name="payment" method="post" action="/donate-options/custom" enctype="utf-8">
        <input type="text" class="input_large"  value="1500" name="amount_rub">
        <input type="submit" value="Пожертвовать эту сумму в рублях">
    </form>
</p>

                    </div>
                </div>
                <div id="sidebar">
                    <script type="text/javascript">CTRManager.show(1, "noauth,nofandome,sfw", "");</script><div class="sidebar_block">
    <h2 class="sideheader">Привет!</h2>
    <div class="sidebarContent">
        <p>Ты тролль, лжец и девственник?</p>
        <p class="login_link"><a href="/register">Присоединяйся!</a></p>
    </div>
</div><div class="sidebar_block">
    <h2 class="sideheader random">
        Юмор    </h2>
    <div class="sidebarContent nopads">
        <ul class="blogs super">
                        <li class="Комиксы">
                <a href="/tag/%25D0%259A%25D0%25BE%25D0%25BC%25D0%25B8%25D0%25BA%25D1%2581%25D1%258B"><strong>Комиксы</strong></a>
                            </li>
                        <li class="гифки">
                <a href="/tag/%25D0%25B3%25D0%25B8%25D1%2584%25D0%25BA%25D0%25B8"><strong>гифки</strong></a>
                            </li>
                        <li class="песочница">
                <a href="/tag/%25D0%25BF%25D0%25B5%25D1%2581%25D0%25BE%25D1%2587%25D0%25BD%25D0%25B8%25D1%2586%25D0%25B0"><strong>песочница</strong></a>
                            </li>
                        <li class="geek">
                <a href="/tag/geek"><strong>geek</strong></a>
                            </li>
                        <li class="котэ">
                <a href="/tag/%25D0%25BA%25D0%25BE%25D1%2582%25D1%258D"><strong>котэ</strong></a>
                            </li>
                        <li class="видео">
                <a href="/tag/%25D0%25B2%25D0%25B8%25D0%25B4%25D0%25B5%25D0%25BE"><strong>видео</strong></a>
                            </li>
                        <li class="story">
                <a href="/tag/story"><strong>story</strong></a>
                            </li>
                    </ul>
    </div>
</div>
<div class="sidebar_block">
    <h2 class="sideheader random">
        Основные разделы    </h2>
    <div class="sidebarContent nopads">
        <ul class="blogs super">
                        <li class="Anime">
                <a href="http://anime.reactor.cc/"><strong>Anime</strong></a>
                            </li>
                        <li class="Эротика">
                <a href="/tag/%25D0%25AD%25D1%2580%25D0%25BE%25D1%2582%25D0%25B8%25D0%25BA%25D0%25B0"><strong>Эротика</strong></a>
                            </li>
                        <li class="красивые картинки">
                <a href="/tag/%25D0%25BA%25D1%2580%25D0%25B0%25D1%2581%25D0%25B8%25D0%25B2%25D1%258B%25D0%25B5%2B%25D0%25BA%25D0%25B0%25D1%2580%25D1%2582%25D0%25B8%25D0%25BD%25D0%25BA%25D0%25B8"><strong>красивые картинки</strong></a>
                            </li>
                        <li class="Игры">
                <a href="/tag/%25D0%2598%25D0%25B3%25D1%2580%25D1%258B"><strong>Игры</strong></a>
                            </li>
                        <li class="anon">
                <a href="/tag/anon"><strong>anon</strong></a>
                            </li>
                        <li class="политота">
                <a href="http://polit.reactor.cc/"><strong>политота</strong></a>
                            </li>
                        <li class="фэндомы">
                <a href="/tag/%25D1%2584%25D1%258D%25D0%25BD%25D0%25B4%25D0%25BE%25D0%25BC%25D1%258B/rating"><strong>фэндомы</strong></a>
                            </li>
                        <li class="разное">
                <a href="/tag/%25D1%2580%25D0%25B0%25D0%25B7%25D0%25BD%25D0%25BE%25D0%25B5/rating"><strong>разное</strong></a>
                            </li>
                    </ul>
    </div>
</div>
<div class="sidebar_block trends_wr">
    <h2 class="sideheader random">
        Собираем на сервера    </h2>
    <div class="sidebarContent">
        В этом месяце собрано         44894 из 160000р.
        <div class="progress_bar">
            <div style="width:28.05875%;" ></div>
        </div>
        <form id="payment" name="payment" method="post" action="/donate" enctype="utf-8">
            <input type="submit" value="Поддержать проект">
        </form>
    </div>
</div>

 <div class="sidebar_block trends_wr"><h2 class="sideheader random"> Тренды </h2><div class="sidebarContent"><div class="blogs"><table><tr><td style="margin-right:5px;"><a title="1 Мая" href="/tag/1%2B%25D0%259C%25D0%25B0%25D1%258F"><img class = "trends" width="100" src="http://img0.joyreactor.cc/pics/avatar/tag/28446" alt="1 Мая"/> </a></td><td><a href="/tag/1%2B%25D0%259C%25D0%25B0%25D1%258F">1 Мая</a></td></tr></table></div></div></div> <div class="sidebar_block"><h2 class="sideheader random"> Наши любимые теги </h2><div class="sidebarContent"><div class="tabs_titles"><ul><li id="blogs_2days"><a href="javascript:void(0);">2 дня</a></li><li id="blogs_week" class="active"><a href="javascript:void(0);">Неделя</a></li><li id="blogs_alltime"><a href="javascript:void(0);">Все время</a></li></ul></div><div class="blogs" id="blogs_alltime_content" style="display:none;"><table><tr><td style="margin-right:5px;"><a title="Warhammer 40000" href="http://wh.reactor.cc/"><img src="http://img0.joyreactor.cc/pics/avatar/tag/3948" alt="Warhammer 40000"/></a></td><td><a href="http://wh.reactor.cc/">Warhammer 40000</a><small>Подписчиков: 6214</small></td></tr><tr><td style="margin-right:5px;"><a title="JaGo" href="/tag/JaGo"><img src="http://img0.joyreactor.cc/pics/avatar/tag/121004" alt="JaGo"/></a></td><td><a href="/tag/JaGo">JaGo</a><small>Подписчиков: 5226</small></td></tr><tr><td style="margin-right:5px;"><a title="countryballs" href="/tag/countryballs"><img src="http://img0.joyreactor.cc/pics/avatar/tag/50612" alt="countryballs"/></a></td><td><a href="/tag/countryballs">countryballs</a><small>Подписчиков: 5066</small></td></tr><tr><td style="margin-right:5px;"><a title="texic" href="/tag/texic"><img src="http://img1.joyreactor.cc/pics/avatar/tag/35485" alt="texic"/></a></td><td><a href="/tag/texic">texic</a><small>Подписчиков: 4809</small></td></tr><tr><td style="margin-right:5px;"><a title="оглаф" href="/tag/%25D0%25BE%25D0%25B3%25D0%25BB%25D0%25B0%25D1%2584"><img src="http://img0.joyreactor.cc/pics/avatar/tag/1336" alt="оглаф"/></a></td><td><a href="/tag/%25D0%25BE%25D0%25B3%25D0%25BB%25D0%25B0%25D1%2584">оглаф</a><small>Подписчиков: 4766</small></td></tr><tr><td style="margin-right:5px;"><a title="Steam халява" href="/tag/Steam%25C2%25A0%25D1%2585%25D0%25B0%25D0%25BB%25D1%258F%25D0%25B2%25D0%25B0"><img src="http://img1.joyreactor.cc/pics/avatar/tag/242057" alt="Steam халява"/></a></td><td><a href="/tag/Steam%25C2%25A0%25D1%2585%25D0%25B0%25D0%25BB%25D1%258F%25D0%25B2%25D0%25B0">Steam халява</a><small>Подписчиков: 4643</small></td></tr><tr><td style="margin-right:5px;"><a title="Этти" href="http://anime.reactor.cc/tag/%25D0%25AD%25D1%2582%25D1%2582%25D0%25B8"><img src="http://img1.joyreactor.cc/pics/avatar/tag/31555" alt="Этти"/></a></td><td><a href="http://anime.reactor.cc/tag/%25D0%25AD%25D1%2582%25D1%2582%25D0%25B8">Этти</a><small>Подписчиков: 4503</small></td></tr><tr><td style="margin-right:5px;"><a title="adventure time" href="http://at.reactor.cc/"><img src="http://img0.joyreactor.cc/pics/avatar/tag/26296" alt="adventure time"/></a></td><td><a href="http://at.reactor.cc/">adventure time</a><small>Подписчиков: 4470</small></td></tr><tr><td style="margin-right:5px;"><a title="Consuls" href="/tag/Consuls"><img src="http://img1.joyreactor.cc/pics/avatar/tag/653237" alt="Consuls"/></a></td><td><a href="/tag/Consuls">Consuls</a><small>Подписчиков: 4449</small></td></tr><tr><td style="margin-right:5px;"><a title="Халява" href="/tag/%25D0%25A5%25D0%25B0%25D0%25BB%25D1%258F%25D0%25B2%25D0%25B0"><img src="http://img1.joyreactor.cc/pics/avatar/tag/2337" alt="Халява"/></a></td><td><a href="/tag/%25D0%25A5%25D0%25B0%25D0%25BB%25D1%258F%25D0%25B2%25D0%25B0">Халява</a><small>Подписчиков: 4332</small></td></tr></table></div><div class="blogs" id="blogs_week_content"><table><tr><td style="margin-right:5px;"><a title="Литературная кухня" href="http://cookreactor.com/tag/%25D0%259B%25D0%25B8%25D1%2582%25D0%25B5%25D1%2580%25D0%25B0%25D1%2582%25D1%2583%25D1%2580%25D0%25BD%25D0%25B0%25D1%258F%2B%25D0%25BA%25D1%2583%25D1%2585%25D0%25BD%25D1%258F"><img src="http://img1.joyreactor.cc/pics/avatar/tag/1075921" alt="Литературная кухня"/></a></td><td><a href="http://cookreactor.com/tag/%25D0%259B%25D0%25B8%25D1%2582%25D0%25B5%25D1%2580%25D0%25B0%25D1%2582%25D1%2583%25D1%2580%25D0%25BD%25D0%25B0%25D1%258F%2B%25D0%25BA%25D1%2583%25D1%2585%25D0%25BD%25D1%258F">Литературная кухня</a><small>Подписчиков: +214</small></td></tr><tr><td style="margin-right:5px;"><a title="ownopgewewg" href="/tag/ownopgewewg"><img src="http://img1.joyreactor.cc/pics/avatar/tag/469427" alt="ownopgewewg"/></a></td><td><a href="/tag/ownopgewewg">ownopgewewg</a><small>Подписчиков: +177</small></td></tr><tr><td style="margin-right:5px;"><a title="Dark Souls 3" href="http://ds.reactor.cc/tag/Dark%2BSouls%2B3"><img src="http://img1.joyreactor.cc/pics/avatar/tag/414531" alt="Dark Souls 3"/></a></td><td><a href="http://ds.reactor.cc/tag/Dark%2BSouls%2B3">Dark Souls 3</a><small>Подписчиков: +121</small></td></tr><tr><td style="margin-right:5px;"><a title="Dark Souls" href="http://ds.reactor.cc/"><img src="http://img1.joyreactor.cc/pics/avatar/tag/32399" alt="Dark Souls"/></a></td><td><a href="http://ds.reactor.cc/">Dark Souls</a><small>Подписчиков: +110</small></td></tr><tr><td style="margin-right:5px;"><a title="Ellen Baker" href="http://anime.reactor.cc/tag/Ellen%2BBaker"><img src="http://img1.joyreactor.cc/pics/avatar/tag/1088893" alt="Ellen Baker"/></a></td><td><a href="http://anime.reactor.cc/tag/Ellen%2BBaker">Ellen Baker</a><small>Подписчиков: +87</small></td></tr><tr><td style="margin-right:5px;"><a title="Battlefield" href="/tag/%2523Battlefield"><img src="http://img1.joyreactor.cc/pics/avatar/tag/1081053" alt="Battlefield"/></a></td><td><a href="/tag/%2523Battlefield">Battlefield</a><small>Подписчиков: +78</small></td></tr><tr><td style="margin-right:5px;"><a title="Будни дружелюбного орка" href="/tag/%25D0%2591%25D1%2583%25D0%25B4%25D0%25BD%25D0%25B8%2B%25D0%25B4%25D1%2580%25D1%2583%25D0%25B6%25D0%25B5%25D0%25BB%25D1%258E%25D0%25B1%25D0%25BD%25D0%25BE%25D0%25B3%25D0%25BE%2B%25D0%25BE%25D1%2580%25D0%25BA%25D0%25B0"><img src="http://img0.joyreactor.cc/pics/avatar/tag/519374" alt="Будни дружелюбного орка"/></a></td><td><a href="/tag/%25D0%2591%25D1%2583%25D0%25B4%25D0%25BD%25D0%25B8%2B%25D0%25B4%25D1%2580%25D1%2583%25D0%25B6%25D0%25B5%25D0%25BB%25D1%258E%25D0%25B1%25D0%25BD%25D0%25BE%25D0%25B3%25D0%25BE%2B%25D0%25BE%25D1%2580%25D0%25BA%25D0%25B0">Будни дружелюбного орка</a><small>Подписчиков: +76</small></td></tr><tr><td style="margin-right:5px;"><a title="Battlefield" href="/tag/Battlefield"><img src="http://img0.joyreactor.cc/pics/avatar/tag/11378" alt="Battlefield"/></a></td><td><a href="/tag/Battlefield">Battlefield</a><small>Подписчиков: +72</small></td></tr><tr><td style="margin-right:5px;"><a title="JoyReactor Games" href="/tag/JoyReactor%2BGames"><img src="http://img1.joyreactor.cc/pics/avatar/tag/50331" alt="JoyReactor Games"/></a></td><td><a href="/tag/JoyReactor%2BGames">JoyReactor Games</a><small>Подписчиков: +53</small></td></tr><tr><td style="margin-right:5px;"><a title="k-eke" href="/tag/k-eke"><img src="http://img0.joyreactor.cc/pics/avatar/tag/1039474" alt="k-eke"/></a></td><td><a href="/tag/k-eke">k-eke</a><small>Подписчиков: +49</small></td></tr></table></div><div class="blogs" id="blogs_2days_content" style="display:none;"><table><tr><td style="margin-right:5px;"><a title="Inquisitior-girlfriend" href="/tag/Inquisitior-girlfriend"><img src="http://img0.joyreactor.cc/images/default_avatar.jpeg" alt="Inquisitior-girlfriend"/></a></td><td><a href="/tag/Inquisitior-girlfriend">Inquisitior-girlfriend</a><small>Подписчиков: +21</small></td></tr><tr><td style="margin-right:5px;"><a title="Fire keeper" href="http://ds.reactor.cc/tag/Fire%2Bkeeper"><img src="http://img0.joyreactor.cc/images/default_avatar.jpeg" alt="Fire keeper"/></a></td><td><a href="http://ds.reactor.cc/tag/Fire%2Bkeeper">Fire keeper</a><small>Подписчиков: +21</small></td></tr><tr><td style="margin-right:5px;"><a title="Чугунные карандаши" href="/tag/%25D0%25A7%25D1%2583%25D0%25B3%25D1%2583%25D0%25BD%25D0%25BD%25D1%258B%25D0%25B5%2B%25D0%25BA%25D0%25B0%25D1%2580%25D0%25B0%25D0%25BD%25D0%25B4%25D0%25B0%25D1%2588%25D0%25B8"><img src="http://img1.joyreactor.cc/pics/avatar/tag/54961" alt="Чугунные карандаши"/></a></td><td><a href="/tag/%25D0%25A7%25D1%2583%25D0%25B3%25D1%2583%25D0%25BD%25D0%25BD%25D1%258B%25D0%25B5%2B%25D0%25BA%25D0%25B0%25D1%2580%25D0%25B0%25D0%25BD%25D0%25B4%25D0%25B0%25D1%2588%25D0%25B8">Чугунные карандаши</a><small>Подписчиков: +19</small></td></tr><tr><td style="margin-right:5px;"><a title="Their Story" href="/tag/Their%2BStory"><img src="http://img0.joyreactor.cc/images/default_avatar.jpeg" alt="Their Story"/></a></td><td><a href="/tag/Their%2BStory">Their Story</a><small>Подписчиков: +18</small></td></tr><tr><td style="margin-right:5px;"><a title="jeff macanoli" href="/tag/jeff%2Bmacanoli"><img src="http://img0.joyreactor.cc/images/default_avatar.jpeg" alt="jeff macanoli"/></a></td><td><a href="/tag/jeff%2Bmacanoli">jeff macanoli</a><small>Подписчиков: +17</small></td></tr><tr><td style="margin-right:5px;"><a title="гифки с предысторией" href="/tag/%25D0%25B3%25D0%25B8%25D1%2584%25D0%25BA%25D0%25B8%2B%25D1%2581%2B%25D0%25BF%25D1%2580%25D0%25B5%25D0%25B4%25D1%258B%25D1%2581%25D1%2582%25D0%25BE%25D1%2580%25D0%25B8%25D0%25B5%25D0%25B9"><img src="http://img1.joyreactor.cc/pics/avatar/tag/411315" alt="гифки с предысторией"/></a></td><td><a href="/tag/%25D0%25B3%25D0%25B8%25D1%2584%25D0%25BA%25D0%25B8%2B%25D1%2581%2B%25D0%25BF%25D1%2580%25D0%25B5%25D0%25B4%25D1%258B%25D1%2581%25D1%2582%25D0%25BE%25D1%2580%25D0%25B8%25D0%25B5%25D0%25B9">гифки с предысторией</a><small>Подписчиков: +15</small></td></tr><tr><td style="margin-right:5px;"><a title="Dark Souls porn" href="/tag/Dark%2BSouls%2Bporn"><img src="http://img0.joyreactor.cc/images/default_avatar.jpeg" alt="Dark Souls porn"/></a></td><td><a href="/tag/Dark%2BSouls%2Bporn">Dark Souls porn</a><small>Подписчиков: +14</small></td></tr><tr><td style="margin-right:5px;"><a title="Игра престолов" href="http://got.reactor.cc/"><img src="http://img0.joyreactor.cc/pics/avatar/tag/11224" alt="Игра престолов"/></a></td><td><a href="http://got.reactor.cc/">Игра престолов</a><small>Подписчиков: +14</small></td></tr><tr><td style="margin-right:5px;"><a title="Undertale" href="http://undertale.reactor.cc/"><img src="http://img1.joyreactor.cc/pics/avatar/tag/897921" alt="Undertale"/></a></td><td><a href="http://undertale.reactor.cc/">Undertale</a><small>Подписчиков: +13</small></td></tr><tr><td style="margin-right:5px;"><a title="Клуб аметистов" href="/tag/%2523%25D0%259A%25D0%25BB%25D1%2583%25D0%25B1%2B%25D0%25B0%25D0%25BC%25D0%25B5%25D1%2582%25D0%25B8%25D1%2581%25D1%2582%25D0%25BE%25D0%25B2"><img src="http://img0.joyreactor.cc/pics/avatar/tag/215616" alt="Клуб аметистов"/></a></td><td><a href="/tag/%2523%25D0%259A%25D0%25BB%25D1%2583%25D0%25B1%2B%25D0%25B0%25D0%25BC%25D0%25B5%25D1%2582%25D0%25B8%25D1%2581%25D1%2582%25D0%25BE%25D0%25B2">Клуб аметистов</a><small>Подписчиков: +13</small></td></tr></table></div></div></div>
    <div class="sidebar_block blogs_wr"><h2 class="sideheader random"> Интересное </h2><div class="sidebarContent"><div class="blogs"><table><tr><td style="margin-right:5px;"><a title="Imperial Knight" href="http://wh.reactor.cc/tag/Imperial%2BKnight"><img src="http://img1.joyreactor.cc/pics/avatar/tag/346947" alt="Imperial Knight"/></a></td><td><a href="http://wh.reactor.cc/tag/Imperial%2BKnight">Imperial Knight</a></td></tr><tr><td style="margin-right:5px;"><a title="tanith first and only" href="http://wh.reactor.cc/tag/tanith%2Bfirst%2Band%2Bonly"><img src="http://img1.joyreactor.cc/pics/avatar/tag/484657" alt="tanith first and only"/></a></td><td><a href="http://wh.reactor.cc/tag/tanith%2Bfirst%2Band%2Bonly">tanith first and only</a></td></tr><tr><td style="margin-right:5px;"><a title="StH gif" href="http://sonic.reactor.cc/tag/StH%2Bgif"><img src="http://img0.joyreactor.cc/pics/avatar/tag/499344" alt="StH gif"/></a></td><td><a href="http://sonic.reactor.cc/tag/StH%2Bgif">StH gif</a></td></tr><tr><td style="margin-right:5px;"><a title="Akairiot" href="/tag/Akairiot"><img src="http://img0.joyreactor.cc/pics/avatar/tag/611900" alt="Akairiot"/></a></td><td><a href="/tag/Akairiot">Akairiot</a></td></tr><tr><td style="margin-right:5px;"><a title="Destroyer Water Oni" href="http://anime.reactor.cc/tag/Destroyer%2BWater%2BOni"><img src="http://img0.joyreactor.cc/pics/avatar/tag/949368" alt="Destroyer Water Oni"/></a></td><td><a href="http://anime.reactor.cc/tag/Destroyer%2BWater%2BOni">Destroyer Water Oni</a></td></tr></table><a href="/tags" class="all-tags">все теги</a></div></div></div> <div class="sidebar_block"><h2 class="sideheader">Топ пользователей</h2><div class="sidebarContent"><div class="tabs_titles"><ul><li id="usertop_week" class="active"><a href="javascript:void(0);">Неделя</a></li><li id="usertop_month"><a href="javascript:void(0);">Месяц</a></li></ul></div><div id="usertop_week_content"><div class="user week_top"><span class="userposition">1</span><a href="/user/VenomRebornZ">VenomRebornZ</a><span class="weekrating">+75.8</span></div><div class="user week_top"><span class="userposition">2</span><a href="/user/sashkauskas">sashkauskas</a><span class="weekrating">+72.1</span></div><div class="user week_top"><span class="userposition">3</span><a href="/user/8l8i8">8l8i8</a><span class="weekrating">+62.6</span></div><div class="user week_top"><span class="userposition">4</span><a href="/user/tenor">tenor</a><span class="weekrating">+43.7</span></div><div class="user week_top"><span class="userposition">5</span><a href="/user/Ponyzbs">Ponyzbs</a><span class="weekrating">+43.0</span></div><div class="user week_top"><span class="userposition">6</span><a href="/user/mrCrAzY">mrCrAzY</a><span class="weekrating">+36.3</span></div><div class="user week_top"><span class="userposition">7</span><a href="/user/Raizel%2BKnight">Raizel Knight</a><span class="weekrating">+34.8</span></div><div class="user week_top"><span class="userposition">8</span><a href="/user/Ser%2BChuvak">Ser Chuvak</a><span class="weekrating">+32.6</span></div><div class="user week_top"><span class="userposition">9</span><a href="/user/prorot">prorot</a><span class="weekrating">+27.7</span></div><div class="user week_top"><span class="userposition">10</span><a href="/user/MagmaTrack">MagmaTrack</a><span class="weekrating">+26.7</span></div><div class="user week_top"><span class="userposition">11</span><a href="/user/Younit13">Younit13</a><span class="weekrating">+24.2</span></div><div class="user week_top"><span class="userposition">12</span><a href="/user/PreyDay">PreyDay</a><span class="weekrating">+23.6</span></div><div class="user week_top"><span class="userposition">13</span><a href="/user/cesar">cesar</a><span class="weekrating">+22.8</span></div><div class="user week_top"><span class="userposition">14</span><a href="/user/Veselyyyy%2Bmalysh">Veselyyyy malysh</a><span class="weekrating">+22.3</span></div><div class="user week_top"><span class="userposition">15</span><a href="/user/Mishvanda">Mishvanda</a><span class="weekrating">+20.2</span></div><div class="user week_top"><span class="userposition">16</span><a href="/user/Darkcross">Darkcross</a><span class="weekrating">+19.8</span></div><div class="user week_top"><span class="userposition">17</span><a href="/user/D_a_R">D_a_R</a><span class="weekrating">+18.8</span></div><div class="user week_top"><span class="userposition">18</span><a href="/user/Desperanto">Desperanto</a><span class="weekrating">+18.3</span></div><div class="user week_top"><span class="userposition">19</span><a href="/user/w1ls0n26">w1ls0n26</a><span class="weekrating">+18.0</span></div><div class="user week_top"><span class="userposition">20</span><a href="/user/ciba">ciba</a><span class="weekrating">+17.9</span></div></div><div id="usertop_month_content" style="display:none;"><div class="user week_top"><span class="userposition">1</span><a href="/user/sashkauskas">sashkauskas</a><span class="weekrating">355.1</span></div><div class="user week_top"><span class="userposition">2</span><a href="/user/VenomRebornZ">VenomRebornZ</a><span class="weekrating">219.6</span></div><div class="user week_top"><span class="userposition">3</span><a href="/user/Raizel%2BKnight">Raizel Knight</a><span class="weekrating">214.1</span></div><div class="user week_top"><span class="userposition">4</span><a href="/user/LYVrus">LYVrus</a><span class="weekrating">193.5</span></div><div class="user week_top"><span class="userposition">5</span><a href="/user/8l8i8">8l8i8</a><span class="weekrating">186.7</span></div><div class="user week_top"><span class="userposition">6</span><a href="/user/PreyDay">PreyDay</a><span class="weekrating">161.2</span></div><div class="user week_top"><span class="userposition">7</span><a href="/user/%252AAchi">*Achi</a><span class="weekrating">144.0</span></div><div class="user week_top"><span class="userposition">8</span><a href="/user/tenor">tenor</a><span class="weekrating">135.7</span></div><div class="user week_top"><span class="userposition">9</span><a href="/user/prorot">prorot</a><span class="weekrating">130.9</span></div><div class="user week_top"><span class="userposition">10</span><a href="/user/Rekolit">Rekolit</a><span class="weekrating">116.4</span></div><div class="user week_top"><span class="userposition">11</span><a href="/user/Mishvanda">Mishvanda</a><span class="weekrating">106.2</span></div><div class="user week_top"><span class="userposition">12</span><a href="/user/ikari">ikari</a><span class="weekrating">95.8</span></div><div class="user week_top"><span class="userposition">13</span><a href="/user/keFF">keFF</a><span class="weekrating">95.4</span></div><div class="user week_top"><span class="userposition">14</span><a href="/user/2pik">2pik</a><span class="weekrating">91.9</span></div><div class="user week_top"><span class="userposition">15</span><a href="/user/plumbym">plumbym</a><span class="weekrating">82.2</span></div><div class="user week_top"><span class="userposition">16</span><a href="/user/erros">erros</a><span class="weekrating">79.3</span></div><div class="user week_top"><span class="userposition">17</span><a href="/user/uni-snake">uni-snake</a><span class="weekrating">78.4</span></div><div class="user week_top"><span class="userposition">18</span><a href="/user/mrCrAzY">mrCrAzY</a><span class="weekrating">78.2</span></div><div class="user week_top"><span class="userposition">19</span><a href="/user/KTyJIXy">KTyJIXy</a><span class="weekrating">72.7</span></div><div class="user week_top"><span class="userposition">20</span><a href="/user/Ghirlandaio">Ghirlandaio</a><span class="weekrating">69.1</span></div></div></div></div><div class="sidebar_block"><h2 class="sideheader">Сейчас на сайте</h2><div class="sidebarContent avatars"><div class="avatar_holder" title="villain2015"><a href="/user/villain2015"><img src="http://img0.joyreactor.cc/pics/avatar/user/90806"/> </a></div><div class="avatar_holder" title="MilaHeat"><a href="/user/MilaHeat"><img src="http://img1.joyreactor.cc/pics/avatar/user/359475"/> </a></div><div class="avatar_holder" title="Artem123546"><a href="/user/Artem123546"><img src="http://img0.joyreactor.cc/images/default_avatar.jpeg"/> </a></div><div class="avatar_holder" title="Lilkrip"><a href="/user/Lilkrip"><img src="http://img1.joyreactor.cc/pics/avatar/user/243839"/> </a></div><div class="avatar_holder" title="jBES"><a href="/user/jBES"><img src="http://img1.joyreactor.cc/pics/avatar/user/173815"/> </a></div><div class="avatar_holder" title="NikolayOss"><a href="/user/NikolayOss"><img src="http://img1.joyreactor.cc/pics/avatar/user/5983"/> </a></div><div class="avatar_holder" title="Nick59"><a href="/user/Nick59"><img src="http://img1.joyreactor.cc/pics/avatar/user/262433"/> </a></div><div class="avatar_holder" title="Triumphator"><a href="/user/Triumphator"><img src="http://img0.joyreactor.cc/pics/avatar/user/25210"/> </a></div><div class="avatar_holder" title="V1G1L4NT3"><a href="/user/V1G1L4NT3"><img src="http://img0.joyreactor.cc/pics/avatar/user/148382"/> </a></div><div class="avatar_holder" title="Комрад"><a href="/user/%25D0%259A%25D0%25BE%25D0%25BC%25D1%2580%25D0%25B0%25D0%25B4"><img src="http://img0.joyreactor.cc/pics/avatar/user/127076"/> </a></div><div class="avatar_holder" title="Felagund"><a href="/user/Felagund"><img src="http://img1.joyreactor.cc/pics/avatar/user/349179"/> </a></div><div class="avatar_holder" title="Tamul"><a href="/user/Tamul"><img src="http://img1.joyreactor.cc/pics/avatar/user/325545"/> </a></div><div class="avatar_holder" title="dingir"><a href="/user/dingir"><img src="http://img0.joyreactor.cc/pics/avatar/user/241866"/> </a></div><div class="avatar_holder" title="Iscariot"><a href="/user/Iscariot"><img src="http://img0.joyreactor.cc/pics/avatar/user/191848"/> </a></div><div class="avatar_holder" title="Kuroske101"><a href="/user/Kuroske101"><img src="http://img1.joyreactor.cc/pics/avatar/user/134923"/> </a></div><div class="avatar_holder" title="terran"><a href="/user/terran"><img src="http://img1.joyreactor.cc/pics/avatar/user/120703"/> </a></div><div class="avatar_holder" title="Tatsu"><a href="/user/Tatsu"><img src="http://img1.joyreactor.cc/pics/avatar/user/41571"/> </a></div><div class="avatar_holder" title="Archer1453"><a href="/user/Archer1453"><img src="http://img0.joyreactor.cc/pics/avatar/user/220248"/> </a></div><div class="avatar_holder" title="Xanac"><a href="/user/Xanac"><img src="http://img1.joyreactor.cc/pics/avatar/user/199755"/> </a></div><div class="avatar_holder" title="a33uggo"><a href="/user/a33uggo"><img src="http://img1.joyreactor.cc/pics/avatar/user/13883"/> </a></div><div class="avatar_holder" title="VenomRebornZ"><a href="/user/VenomRebornZ"><img src="http://img1.joyreactor.cc/pics/avatar/user/439575"/> </a></div><div class="avatar_holder" title="CandyStrike"><a href="/user/CandyStrike"><img src="http://img1.joyreactor.cc/pics/avatar/user/27233"/> </a></div><div class="avatar_holder" title="FrostBy"><a href="/user/FrostBy"><img src="http://img1.joyreactor.cc/pics/avatar/user/18445"/> </a></div><div class="avatar_holder" title="Puzzle"><a href="/user/Puzzle"><img src="http://img0.joyreactor.cc/images/default_avatar.jpeg"/> </a></div><div class="avatar_holder" title="Zipers"><a href="/user/Zipers"><img src="http://img0.joyreactor.cc/pics/avatar/user/59574"/> </a></div><div class="avatar_holder" title="orange demon"><a href="/user/orange%2Bdemon"><img src="http://img0.joyreactor.cc/pics/avatar/user/246592"/> </a></div><div class="avatar_holder" title="AGIMgal"><a href="/user/AGIMgal"><img src="http://img1.joyreactor.cc/pics/avatar/user/16861"/> </a></div><div class="avatar_holder" title="Dorh"><a href="/user/Dorh"><img src="http://img0.joyreactor.cc/images/default_avatar.jpeg"/> </a></div><div class="avatar_holder" title="ZapWagon"><a href="/user/ZapWagon"><img src="http://img0.joyreactor.cc/pics/avatar/user/62194"/> </a></div><div class="avatar_holder" title="mrCrAzY"><a href="/user/mrCrAzY"><img src="http://img1.joyreactor.cc/pics/avatar/user/73323"/> </a></div><div class="avatar_holder" title="VLADGANFITER"><a href="/user/VLADGANFITER"><img src="http://img0.joyreactor.cc/pics/avatar/user/114200"/> </a></div><div class="avatar_holder" title="A new life"><a href="/user/A%2Bnew%2Blife"><img src="http://img1.joyreactor.cc/pics/avatar/user/428269"/> </a></div><div class="avatar_holder" title="BoecSpecOPs"><a href="/user/BoecSpecOPs"><img src="http://img1.joyreactor.cc/pics/avatar/user/42717"/> </a></div><div class="avatar_holder" title="MrCheat"><a href="/user/MrCheat"><img src="http://img0.joyreactor.cc/pics/avatar/user/33004"/> </a></div><div class="avatar_holder" title="ScrewU"><a href="/user/ScrewU"><img src="http://img1.joyreactor.cc/pics/avatar/user/30027"/> </a></div><div class="avatar_holder" title="Imebal"><a href="/user/Imebal"><img src="http://img1.joyreactor.cc/pics/avatar/user/54947"/> </a></div><div class="avatar_holder" title="Дозоныч"><a href="/user/%25D0%2594%25D0%25BE%25D0%25B7%25D0%25BE%25D0%25BD%25D1%258B%25D1%2587"><img src="http://img1.joyreactor.cc/pics/avatar/user/345287"/> </a></div><div class="avatar_holder" title="Unleash"><a href="/user/Unleash"><img src="http://img0.joyreactor.cc/pics/avatar/user/151724"/> </a></div><div class="avatar_holder" title="h3kon"><a href="/user/h3kon"><img src="http://img0.joyreactor.cc/pics/avatar/user/381720"/> </a></div><br/> Всего пользователей на сайте: 2546 </div></div><script type="text/javascript">CTRManager.show(4, "noauth,nofandome,sfw", "");</script>   <!--div class="sidebar_block">

        <script language="javascript" type="text/javascript" src="http://userapi.com/js/api/openapi.js?47"></script>

        <div id="vk_groups"></div>
        <script type="text/javascript">
            VK.Widgets.Group("vk_groups", {mode: 1, width: "300", height: "290"}, 34113013);
        </script>
    </div-->


<div class="sidebar_block">
<!--LiveInternet counter--><script type="text/javascript"><!--
    document.write("<a href='http://www.liveinternet.ru/click;JoyReactor' "+
        "target=_blank><img src='//counter.yadro.ru/hit;JoyReactor?t26.6;r"+
        escape(document.referrer)+((typeof(screen)=="undefined")?"":
        ";s"+screen.width+"*"+screen.height+"*"+(screen.colorDepth?
            screen.colorDepth:screen.pixelDepth))+";u"+escape(document.URL)+
        ";"+Math.random()+
        "' alt='' "+
        "border='0' width='88' height='15'><\/a>")
    //--></script><!--/LiveInternet-->
</div><script type="text/javascript">CTRManager.show(8, "noauth,nofandome,sfw", "");</script>                </div>
                            </div>
        </div>
        <div id="footer">
        </div>
    </div>
</div>

<script type="text/javascript">
    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
    try {
        var pageTracker = _gat._getTracker("UA-5461980-2");
        pageTracker._trackPageview();
    } catch(err) {}
</script>



    <div id="SignInOrSignUpDialog" title="Велком!" class="qtipped" style="display: none;">
              <form method="POST" action="/login" name="sf_guard_signin" id="sf_guard_signin" class="sfSignin">
  <li>
  <label for="signin_username">Логин:</label>
  <input type="text" name="signin[username]" id="signin_username" />
</li>
<li>
  <label for="signin_password">Пароль:</label>
  <input type="password" name="signin[password]" id="signin_password" />
</li>
<li>

  <input type="checkbox" name="signin[remember]" checked="checked" id="signin_remember" /><label for="signin_remember">Запомнить меня</label>
<input type="hidden" name="signin[_csrf_token]" value="a5ad398a8a9f4a9372a0ac7d0bb74c9c" id="signin__csrf_token" /></li>
  <input type="submit" value="Войти" />
</form>
          <br/>
        Нет аккаунта? <a href="/register">Регистрация</a>    </div>
</body>
</html>"""