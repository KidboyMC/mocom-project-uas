package com.example.nobarek.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nobarek.data.local.UserEntity
import com.example.nobarek.viewmodel.UserViewModel

// ✅ UPDATED - Menerima UserViewModel parameter
@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    onLogoutClick: () -> Unit
) {
    val user by userViewModel.loggedInUser.collectAsState()
    val scrollState = rememberScrollState()
    val backgroundColor = Color(0xFFEAEAEA)
    val accentColor = Color(0xFFFFC107)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState)
            .statusBarsPadding()
    ) {
        // 1. HEADER dengan data user real
        ProfileHeaderSection(user)
        Spacer(modifier = Modifier.height(24.dp))

        // 2. PREMIUM CARD
        PremiumMemberCard(accentColor)
        Spacer(modifier = Modifier.height(24.dp))

        // 3. STATISTIK PENGGUNA (Real Data)
        UserStatsSection(user)
        Spacer(modifier = Modifier.height(24.dp))

        // 4. MENU SETTINGS
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader("General")
            ProfileMenuItem(icon = Icons.Default.Person, title = "Account", subtitle = "Security, Personal Info")
            ProfileMenuItem(icon = Icons.Default.Notifications, title = "Notifications", subtitle = "News, Newsletters")
            ProfileMenuItem(icon = Icons.Default.Download, title = "Download Settings", subtitle = "Wi-Fi Only, Quality")

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader("Support")
            ProfileMenuItem(icon = Icons.AutoMirrored.Filled.Help, title = "Help Center", subtitle = "FAQ, Contact Us")
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Sign Out",
                subtitle = "",
                isDestructive = true,
                onClick = onLogoutClick
            )

            Spacer(modifier = Modifier.height(100.dp)) // Padding bawah agar tidak tertutup navbar
        }
    }
}

@Composable
fun ProfileHeaderSection(user: UserEntity?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Box(contentAlignment = Alignment.BottomEnd) {
            // ✅ Avatar dinamis berdasarkan username
            AsyncImage(
                model = "https://ui-avatars.com/api/?name=${user?.username ?: "User"}+Nobarek&background=0D8ABC&color=fff&size=256",
                contentDescription = "Profile",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(24.dp))
            )

            Surface(
                color = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = 4.dp, y = 4.dp)
                    .border(2.dp, Color.White, CircleShape)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Nama dari database
        Text(
            text = user?.username ?: "User",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // ✅ Email dari username + domain
        Text(
            text = "${user?.username ?: "user"}@nobarek.com",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        // ✅ Role indicator
        if (user?.role == "admin") {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color(0xFFFFC107).copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "ADMIN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC107)
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumMemberCard(accentColor: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "PREMIUM MEMBER",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "Upgrade for 4K Quality",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Details", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UserStatsSection(user: UserEntity?) {
    // ✅ Statistik real dari database (placeholder untuk saat ini)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem("0", "Watched")
        StatItem("0", "Favorites")
        StatItem("0", "Reviews")
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp),
        color = Color.Black
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Surface(
                color = if (isDestructive) Color(0xFFFFEBEE) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDestructive) Color.Red else Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (isDestructive) Color.Red else Color.Black
                )

                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}