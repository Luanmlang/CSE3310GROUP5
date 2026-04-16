package com.example.loginandprofile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userData: UserData,
    onSave: (UserData) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    var username        by remember { mutableStateOf(userData.username) }
    var email           by remember { mutableStateOf(userData.email) }
    var newPassword     by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var imageUri        by remember {
        mutableStateOf(userData.profileImageUri.takeIf { it.isNotEmpty() })
    }
    var saveMessage        by remember { mutableStateOf("") }
    var showLogoutDialog   by remember { mutableStateOf(false) }

    // Load bitmap from URI without any external library
    val profileBitmap = remember(imageUri) {
        imageUri?.let { uriString ->
            runCatching {
                context.contentResolver
                    .openInputStream(Uri.parse(uriString))
                    ?.use { BitmapFactory.decodeStream(it) }
            }.getOrNull()
        }
    }

    val accentColor     = Color(0xFF1A73E8)
    val backgroundColor = Color(0xFFF5F5F5)

    // Gallery picker
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { imageUri = it.toString() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile", fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, color = Color(0xFF1A1A2E))
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        // Fixed: use AutoMirrored version
                        Icon(Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Profile picture ──────────────────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF1A73E8), Color(0xFF0D47A1))
                            )
                        )
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileBitmap != null) {
                        // Built-in bitmap display — no coil needed
                        Image(
                            bitmap = profileBitmap.asImageBitmap(),
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                // "+" button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Pick image",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(userData.username, fontSize = 18.sp,
                fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))

            Spacer(modifier = Modifier.height(24.dp))

            // ── Settings card ────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {

                    Text("Account Settings", fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E),
                        modifier = Modifier.padding(bottom = 20.dp))

                    // Username
                    PLabel("Username:")
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it; saveMessage = "" },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Change username") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, null, tint = accentColor) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = pFieldColors(accentColor)
                    )

                    Spacer(Modifier.height(16.dp))

                    // Email
                    PLabel("Email:")
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; saveMessage = "" },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Change email") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = accentColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = pFieldColors(accentColor)
                    )

                    Spacer(Modifier.height(16.dp))

                    // New password
                    PLabel("New Password:")
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it; saveMessage = "" },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Leave blank to keep current") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = accentColor) },
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(if (passwordVisible) "Hide" else "Show",
                                    fontSize = 12.sp, color = accentColor,
                                    fontWeight = FontWeight.Medium)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = pFieldColors(accentColor)
                    )

                    if (saveMessage.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(saveMessage,
                            color = if (saveMessage.startsWith("✓")) Color(0xFF2E7D32) else Color.Red,
                            fontSize = 13.sp)
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            when {
                                username.isBlank() ->
                                    saveMessage = "Username cannot be empty."
                                email.isBlank() || !email.contains("@") ->
                                    saveMessage = "Enter a valid email."
                                newPassword.isNotEmpty() && newPassword.length < 6 ->
                                    saveMessage = "Password must be at least 6 characters."
                                else -> {
                                    onSave(userData.copy(
                                        username = username,
                                        email = email,
                                        password = newPassword.ifEmpty { userData.password },
                                        profileImageUri = imageUri ?: ""
                                    ))
                                    newPassword = ""
                                    saveMessage = "✓ Profile saved successfully!"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.Red)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null,
                    tint = Color.Red, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Logout", fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold, color = Color.Red)
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text  = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = onLogout) {
                    Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable private fun PLabel(label: String) {
    Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium,
        color = Color(0xFF555555), modifier = Modifier.fillMaxWidth())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun pFieldColors(accent: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = accent, unfocusedBorderColor = Color(0xFFDDDDDD)
)