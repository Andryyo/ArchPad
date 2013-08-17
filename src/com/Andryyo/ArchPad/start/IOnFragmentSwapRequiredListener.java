package com.Andryyo.ArchPad.start;

import android.os.Bundle;
import com.Andryyo.ArchPad.archeryFragment.CRoundTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 21.07.13
 * Time: 15:34
 * To change this template use File | Settings | File Templates.
 */
public interface IOnFragmentSwapRequiredListener {
    public void startArcheryFragment(CRoundTemplate template);
    public void startStartFragment();
}
