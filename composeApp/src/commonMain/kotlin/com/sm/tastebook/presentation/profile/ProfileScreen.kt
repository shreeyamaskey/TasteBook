package com.sm.tastebook.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import tastebook.composeapp.generated.resources.Res
import tastebook.composeapp.generated.resources.banner
import tastebook.composeapp.generated.resources.profile_placeholder


// Add these imports
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onEditProfileClick: () -> Unit = {},
    onYourRecipesClick: () -> Unit = {},
    onYourCommunityClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Banner and Profile Image
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            // Banner Image
            Image(
                painter = painterResource(Res.drawable.banner),
                contentDescription = "Profile Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Profile Image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .align(Alignment.BottomStart)
                    .offset(x = 0.dp, y = 0.dp),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for profile image
                Image(
                    painter = painterResource(Res.drawable.profile_placeholder),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
        }
        
        // Add some space after the profile image
        Spacer(modifier = Modifier.height(20.dp))
        
        // User Information Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (state.error != null) {
                Text(
                    text = state.error ?: "Error loading profile",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                // User's full name
                Text(
                    text = "${state.firstName} ${state.lastName}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Username
                Text(
                    text = "@${state.username}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Email
                Text(
                    text = state.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // User's Sections (Recipes and Community)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Your Recipes Section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { onYourRecipesClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View your recipes",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            
            // Your Community Section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { onYourCommunityClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View your community",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(30.dp))
        
        // Profile Actions - Only Edit Profile button, centered
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            // Edit Profile Button
            Button(
                onClick = onEditProfileClick,
                modifier = Modifier
                    .fillMaxWidth(0.6f)  // Take up half the width
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

    }
}