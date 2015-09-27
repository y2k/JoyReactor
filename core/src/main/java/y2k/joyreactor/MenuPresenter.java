package y2k.joyreactor;

/**
 * Created by y2k on 9/26/15.
 */
public class MenuPresenter extends Presenter {

    private final View view;
    public Tag.Collection tags;

    public MenuPresenter(View view) {
        this.view = view;

        tags = new TagLoader().loadTags();
        view.reloadData(tags);
    }

    @Override
    public void activate() {
        view.reloadData(tags);
    }

    public interface View {

        void reloadData(Tag.Collection tags);
    }

    static class TagLoader {

        Tag.Collection tags = new Tag.Collection();

        public Tag.Collection loadTags() {
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

            return tags;
        }

        private void addTag(String title, String tagId) {
            Tag tag = new Tag();
            tag.title = title;
            tag.image = "http://img0.joyreactor.cc/pics/avatar/tag/" + tagId;
            tags.add(tag);
        }
    }
}