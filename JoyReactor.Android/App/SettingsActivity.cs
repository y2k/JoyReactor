using Android.Preferences;
using Android.OS;
using Android.App;

namespace JoyReactor.Android.App
{
    [Activity(Label = "@string/settings")]
    public class SettingsActivity : PreferenceActivity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            PreferenceScreen = PreferenceManager.CreatePreferenceScreen(this);
            PreferenceScreen.AddPreference(
                new EditTextPreference(this)
                { 
                    Title = "Proxy hostname",
                    Summary = "If url can't be accepted direct, will be used proxy",
                });
            PreferenceScreen.AddPreference(
                new CheckBoxPreference(this)
                { 
                    Title = "Use free SSL proxy",
                    Summary = "Without warranty of any kind",
                });
            PreferenceScreen.AddPreference(
                new Preference(this)
                { 
                    Title = GetString(Resource.String.about),
                });
        }
    }
}