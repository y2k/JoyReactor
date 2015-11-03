package y2k.joyreactor.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.PostListSynchronizer;
import y2k.joyreactor.Repository;
import y2k.joyreactor.common.PostGenerator;

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
        FIRST_PAGE = PostGenerator.getMockFirstPage(0);
        NEXT_PAGE = PostGenerator.getMockFirstPage(FIRST_PAGE.size());

        MockitoAnnotations.initMocks(this);

        when(synchronizerFactory.make()).thenReturn(synchronizer);
    }


    @Test
    public void testLoadFirstPage() throws Exception {
        when(mockRepository.queryAsync()).thenReturn(Observable.just(Collections.emptyList()));

        when(synchronizer.preloadNewPosts()).thenReturn(Observable.just(false));
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