package com.ontime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ontime.ui.theme.CardSurface
import com.ontime.ui.theme.PrimaryWhite
import java.time.LocalTime

@Composable
fun TimeBlockInput(
    startTime: LocalTime,
    endTime: LocalTime,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = startTime.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryWhite
            )

            Text(
                text = " — ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryWhite,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Text(
                text = endTime.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryWhite
            )
        }
    }
}
