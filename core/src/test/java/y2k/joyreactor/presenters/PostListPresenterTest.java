package y2k.joyreactor.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import y2k.joyreactor.Post;
import y2k.joyreactor.Repository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by y2k on 11/2/15.
 */
public class PostListPresenterTest {

    @Before
    public void setUp() throws Exception {
        PostListPresenter.View mockView = mock(PostListPresenter.View.class);
        Repository<Post> mockRepository = mock(Repository.class);
        new PostListPresenter(mockView, mockRepository);
    }

    @Test
    public void testApplyNew() throws Exception {
        // TODO:
    }
}