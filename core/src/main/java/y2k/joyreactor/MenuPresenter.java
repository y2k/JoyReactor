package y2k.joyreactor;

/**
 * Created by y2k on 9/26/15.
 */
public class MenuPresenter {

    public final Tag.Collection tags = new Tag.Collection();

    public MenuPresenter(View view) {
        addTag("Anime", "2851");
        addTag("Эротика", "676");
        addTag("Красивые картинки", "31505");
        addTag("Игры", "753");
        addTag("Anon", "22045");
        addTag("Политота", "3443");

        addTag("Комиксы", "27");
        addTag("Гифки", "116");
        addTag("Песочница", "10891");
        addTag("Geek", "7");
        addTag("Котэ", "1481");
        addTag("Видео", "1243");
        addTag("Story", "227");

        view.reloadData();
    }

    private void addTag(String title, String tagId) {
        Tag tag = new Tag();
        tag.title = title;
        tag.image = "http://img0.joyreactor.cc/pics/avatar/tag/" + tagId;
        tags.add(tag);
    }

    public interface View {

        void reloadData();
    }
}