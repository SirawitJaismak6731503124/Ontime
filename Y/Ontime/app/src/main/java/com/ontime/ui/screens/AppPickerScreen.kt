package com.ontime.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.ontime.data.model.LaunchableApp
import com.ontime.ui.theme.DarkGrey
import com.ontime.ui.theme.DeepBlack
import com.ontime.ui.theme.HighlightWhite
import com.ontime.ui.theme.LightGrey
import com.ontime.ui.theme.SurfaceGrey
import com.ontime.ui.theme.WhiteText

@Composable
fun AppPickerScreen(
    apps: List<LaunchableApp>,
    selectedPackages: Set<String>,
    onBack: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    val selectedState = remember(selectedPackages) {
        mutableStateListOf<String>().apply { addAll(selectedPackages) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Choose apps", color = WhiteText, fontSize = 20.sp)
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = SurfaceGrey)) {
                Text(text = "Back", color = WhiteText)
            }
        }

        Text(
            text = "Only launcher apps are shown. Package names are stored for monitoring.",
            color = LightGrey,
            fontSize = 13.sp
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            items(apps, key = { it.packageName }) { app ->
                val isSelected = selectedState.contains(app.packageName)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) selectedState.remove(app.packageName) else selectedState.add(app.packageName)
                        },
                    colors = CardDefaults.cardColors(containerColor = DarkGrey),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            bitmap = app.icon.toBitmap().asImageBitmap(),
                            contentDescription = app.appName,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = app.appName, color = WhiteText, fontSize = 16.sp)
                            Text(text = app.packageName, color = LightGrey, fontSize = 12.sp)
                        }
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedState.add(app.packageName)
                                } else {
                                    selectedState.remove(app.packageName)
                                }
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = { onConfirm(selectedState.toSet()) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = HighlightWhite)
        ) {
            Text(text = "Use selected apps", color = DeepBlack)
        }
    }
}