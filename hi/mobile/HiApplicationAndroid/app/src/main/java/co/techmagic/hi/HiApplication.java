package co.techmagic.hi;

import android.app.Application;

import com.parse.Parse;

public class HiApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this,
                "TMG49CgTdo2aVoCXf6YWTnA9zwiQa68aPDblnOJv",
                "hfM1gHyhvoWy389gSFBi3aKQx1awTxL5v9RlDujW"
        );
    }
}
