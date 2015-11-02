package y2k.joyreactor.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.Repository;
import y2k.joyreactor.requests.PostsForTagRequest;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by y2k on 11/2/15.
 */
public class PostListPresenterTest {

    @Before
    public void setUp() throws Exception {
        PostListPresenter.View mockView = mock(PostListPresenter.View.class);

        Repository<Post> mockRepository = mock(Repository.class);
        when(mockRepository.queryAsync()).thenReturn(Observable.just(new ArrayList<>()));
        when(mockRepository.replaceAllAsync(any())).thenReturn(Observable.just(null));

        PostsForTagRequest mockRequest = mock(PostsForTagRequest.class);
        when(mockRequest.getPosts()).thenReturn(new ArrayList<>());
        when(mockRequest.requestAsync()).thenReturn(Observable.just(null));

        PostsForTagRequest.Factory mockFactory = mock(PostsForTagRequest.Factory.class);
        when(mockFactory.make(anyString(), anyString())).thenReturn(mockRequest);

        new PostListPresenter(mockView, mockRepository, mockFactory);
    }

    @Test
    public void testApplyNew() throws Exception {
        // TODO:
    }
}