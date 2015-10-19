package y2k.joyreactor;

/**
 * Created by y2k on 06/10/15.
 */
public class Messages {

    public static class TagSelected {

        public Tag tag;

        public TagSelected(Tag tag) {
            this.tag = tag;
        }

        public void broadcast() {
            Messenger.getInstance().send(this);
        }
    }
}