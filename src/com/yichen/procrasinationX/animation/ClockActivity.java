/* Copyright (c) 2002-2014 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package com.yichen.procrasinationX.animation;

import com.yichen.procrasinationX.R;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

/**
 * Simple example for the animation framework. A soccer ball bounces from the
 * top/left corner of the screen to the lower/right corner. This example uses an
 * ObjectAnimator to animate the x and y positions of an ImageView. It shows the
 * animation via an XML resource as well as an identical version using Java API.
 */
public class ClockActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation);

        /**
         * Find the ImageView that shows the soccer ball.
         */
        ImageView clock = (ImageView) this.findViewById(R.id.clock);

        
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bounce);
        set.setTarget(clock);
        set.start();
    }

    
    private void animateViaAPI(ImageView ball) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(ball, "xFraction", 0f, 1f).setDuration(5000);
        ObjectAnimator animY = ObjectAnimator.ofFloat(ball, "yFraction", 0f, 1f).setDuration(5000);
        animY.setInterpolator(new BounceInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(animX).with(animY);
        set.start();
    }
}
