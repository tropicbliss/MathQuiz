package net.tropicbliss.mathquiz.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.tropicbliss.mathquiz.R

@Composable
fun EmptyList(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier
            .padding(
                dimensionResource(R.dimen.padding_medium)
            )
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Create,
            contentDescription = stringResource(R.string.empty_list),
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = stringResource(R.string.empty_list_msg),
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center
        )
    }
}