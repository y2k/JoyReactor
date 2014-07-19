using JoyReactor.WP.ViewModel;
using System.Windows.Navigation;

namespace JoyReactor.WP.View
{
    public partial class SinglePostPage : BasePage
    {
        public SinglePostPage()
        {
            InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            base.OnNavigatedTo(e);

            ((SinglePostViewModel)DataContext).LoadData(int.Parse(NavigationContext.QueryString["id"]));
        }
    }
}