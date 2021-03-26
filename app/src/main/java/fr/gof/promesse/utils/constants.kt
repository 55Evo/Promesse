
package utils

import android.graphics.Color
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import fr.gof.promesse.model.Mascot
import fr.gof.promesse.model.User



const val NOTIFICATION_CHANNEL_ID = "100"
const val LEFT = 4
const val RIGHT = 8
var  config : SlidrConfig =  SlidrConfig.Builder()
    .position(SlidrPosition.TOP)
    .sensitivity(1f)
    .scrimColor(Color.BLACK)
    .scrimStartAlpha(0.9f)
    .scrimEndAlpha(0f)
    .velocityThreshold(1000F)
    .distanceThreshold(0.5f)
    .edge(true)
    .edgeSize(0.15f) // The % of the screen that counts as the edge, default 18%
    .build();