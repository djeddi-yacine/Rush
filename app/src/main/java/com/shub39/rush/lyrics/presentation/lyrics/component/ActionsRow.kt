package com.shub39.rush.lyrics.presentation.lyrics.component

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shub39.rush.R
import com.shub39.rush.core.data.SongDetails
import com.shub39.rush.core.domain.Sources
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageAction
import com.shub39.rush.lyrics.presentation.lyrics.LyricsPageState
import com.shub39.rush.lyrics.presentation.lyrics.SongUi
import com.shub39.rush.lyrics.presentation.lyrics.copyToClipBoard

@Composable
fun ActionsRow(
    state: LyricsPageState,
    context: Context,
    song: SongUi,
    action: (LyricsPageAction) -> Unit,
    notificationAccess: Boolean,
    cardBackground: Color,
    cardContent: Color,
    onShare: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    if (state.selectedLines.isEmpty()) {
                        copyToClipBoard(
                            context,
                            if (state.source == Sources.LrcLib) {
                                song.lyrics.joinToString("\n") { it.value }
                            } else {
                                song.geniusLyrics?.joinToString("\n") { it.value }
                                    ?: ""
                            },
                            "Complete Lyrics"
                        )
                    } else {
                        copyToClipBoard(
                            context,
                            state.selectedLines.toSortedMap().values.joinToString(
                                "\n"
                            ),
                            "Selected Lyrics"
                        )
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_content_copy_24),
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = state.selectedLines.isEmpty()) {
                IconButton(onClick = {
                    action(
                        LyricsPageAction.OnSourceChange(
                            if (state.source == Sources.LrcLib) Sources.Genius else Sources.LrcLib
                        )
                    )

                    action(
                        LyricsPageAction.OnSync(false)
                    )
                }) {
                    if (state.source == Sources.Genius) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_lyrics_24),
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.genius),
                            contentDescription = null
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = state.source == Sources.LrcLib && state.selectedLines.isEmpty()
            ) {
                IconButton(
                    onClick = {
                        action(
                            LyricsPageAction.OnLyricsCorrect(true)
                        )
                        action(
                            LyricsPageAction.OnSync(false)
                        )
                        if (state.autoChange) action(
                            LyricsPageAction.OnToggleAutoChange
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_edit_note_24),
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(
                visible = state.syncedAvailable && state.selectedLines.isEmpty() && state.source == Sources.LrcLib && notificationAccess
            ) {
                Row {
                    IconButton(
                        onClick = {
                            action(
                                LyricsPageAction.OnSync(!state.sync)
                            )
                        },
                        colors = if (state.sync) {
                            IconButtonDefaults.iconButtonColors(
                                contentColor = cardBackground,
                                containerColor = cardContent
                            )
                        } else {
                            IconButtonDefaults.iconButtonColors()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_sync_24),
                            contentDescription = null
                        )
                    }
                }
            }

            AnimatedVisibility(visible = notificationAccess) {
                IconButton(
                    onClick = { action(LyricsPageAction.OnToggleAutoChange) },
                    colors = if (state.autoChange) {
                        IconButtonDefaults.iconButtonColors(
                            contentColor = cardBackground,
                            containerColor = cardContent
                        )
                    } else {
                        IconButtonDefaults.iconButtonColors()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.rush_transparent),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            AnimatedVisibility(visible = state.selectedLines.isNotEmpty()) {
                Row {
                    IconButton(onClick = {
                        action(
                            LyricsPageAction.OnUpdateShareLines(
                                songDetails = SongDetails(
                                    title = song.title,
                                    artist = song.artists,
                                    album = song.album,
                                    artUrl = song.artUrl ?: ""
                                )
                            )
                        )
                        onShare()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_share_24),
                            contentDescription = null
                        )
                    }

                    IconButton(onClick = {
                        action(LyricsPageAction.OnChangeSelectedLines(emptyMap()))
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}