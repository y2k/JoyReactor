package y2k.joyreactor.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.PostListSynchronizer;
import y2k.joyreactor.PostMerger;
import y2k.joyreactor.Repository;
import y2k.joyreactor.requests.PostsForTagRequest;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by y2k on 11/2/15.
 */
public class PostListPresenterTest {

    List<Post> FIRST_PAGE;
    List<Post> NEXT_PAGE;

    @Mock
    PostListPresenter.View mockView;
    @Mock
    Repository<Post> mockRepository;

    @Mock
    PostListSynchronizer.Factory synchronizerFactory;
    @Mock
    PostListSynchronizer synchronizer;

    @Captor
    ArgumentCaptor<List<Post>> captor;

    PostListPresenter presenter;

    @Before
    public void setUp() throws Exception {
        FIRST_PAGE = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            FIRST_PAGE.add(makePost("" + i));
        NEXT_PAGE = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            NEXT_PAGE.add(makePost("" + (FIRST_PAGE.size() + i)));

        MockitoAnnotations.initMocks(this);

        when(synchronizerFactory.make()).thenReturn(synchronizer);
    }

    private Post makePost(String id) {
        Post p = new Post();
        p.id = id;
        return p;
    }

    @Test
    public void testLoadFirstPage() throws Exception {
        when(mockRepository.queryAsync()).thenReturn(Observable.just(Collections.emptyList()));

        when(synchronizer.checkIsUnsafeReload()).thenReturn(Observable.just(false));
        when(synchronizer.applyNew()).then(s -> {
            when(mockRepository.queryAsync()).thenReturn(Observable.just(FIRST_PAGE));
            when(synchronizer.getDivider()).thenReturn(FIRST_PAGE.size());
            return Observable.just(null);
        });

        presenter = new PostListPresenter(mockView, mockRepository, synchronizerFactory);

        verify(mockView).reloadPosts(captor.capture(), isNull(Integer.class));
        assertEquals(0, captor.getValue().size());

        verify(mockView).reloadPosts(captor.capture(), eq(10));
        assertEquals(10, captor.getValue().size());
    }

    @Test
    public void testLoadNextPage() throws Exception {
        testLoadFirstPage();

//        when(synchronizer.loadNextPage()).thenReturn(Observable.just(null));
        when(synchronizer.loadNextPage()).then(s -> {
            List<Post> union = new ArrayList<>(FIRST_PAGE);
            union.addAll(NEXT_PAGE);

            when(mockRepository.queryAsync()).thenReturn(Observable.just(union));
            when(synchronizer.getDivider()).thenReturn(union.size());
            return Observable.just(null);
        });

        presenter.loadMore();

        verify(mockView).reloadPosts(captor.capture(), eq(20));
        assertEquals(20, captor.getValue().size());
    }
}