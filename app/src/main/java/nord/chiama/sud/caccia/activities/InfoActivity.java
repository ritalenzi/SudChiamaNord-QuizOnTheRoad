package nord.chiama.sud.caccia.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import nord.chiama.sud.caccia.R;
import nord.chiama.sud.caccia.utils.LifecycleLoggingActivity;

public class InfoActivity extends LifecycleLoggingActivity
{

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView(R.layout.activity_info);

    }

    private void openWebPage (Uri uri)
    {
        Intent intent = new Intent (Intent.ACTION_VIEW, uri);
        startActivity (intent);
    }

    public void viewRules (View view)
    {
        openWebPage (Uri.parse ("http://www.sudchiamanord.com/quizontheroad/Regolamento_QuizOnTheRoad.pdf"));
    }

    public void viewScnWebsite (View view)
    {
        openWebPage (Uri.parse ("http://www.sudchiamanord.com"));
    }

    public void openSponsorLink (View view)
    {
        ImageView imageView = (ImageView) view;
        Uri uri;

        switch (imageView.getId()) {
            case R.id.sponsor1:
                uri = Uri.parse ("http://www.3csrl.com/default.php?t=site&pgid=395");
                break;

            case R.id.sponsor2:
                uri = Uri.parse ("http://www.automazionibigliardi.it/");
                break;

            case R.id.sponsor3:
                uri = Uri.parse ("http://www.bper.it/");
                break;

            case R.id.sponsor4:
                uri = Uri.parse ("http://www.facebook.com/pages/Osteria-La-Cirenaica/287624454649827");
                break;

            case R.id.sponsor5:
                uri = Uri.parse ("http://www.torellitours.it/");
                break;

            default:
                return;
        }

        openWebPage (uri);
    }
}
