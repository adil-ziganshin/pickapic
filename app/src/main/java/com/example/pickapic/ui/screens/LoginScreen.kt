package com.example.pickapic.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pickapic.R
import com.example.pickapic.ui.navigation.HomeRoute
import com.example.pickapic.ui.navigation.RegistrationRoute
import com.example.pickapic.ui.composables.TitleCard
import com.example.pickapic.ui.theme.*

@Composable
fun LoginScreen(
    navController: NavController
) {

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    PickapicTheme {
        Scaffold(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = contentColorFor(SnackbarDefaults.backgroundColor)
                .takeOrElse { LocalContentColor.current }
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_login_background),
                    contentDescription = "Background",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp)
                        .scale(1.45f)
                        .align(Alignment.BottomEnd)
                )
                TitleCard(text = stringResource(id = R.string.login), color = Pencil700)
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 64.dp)
                ) {
                    OutlinedTextField(
                        value = login,
                        onValueChange = { login = it },
                        label = { Text("Login") },
                        modifier = Modifier.padding(2.dp),
                        trailingIcon = {Icon(imageVector = Icons.Rounded.Email, contentDescription = "Login")},
                        shape = Shapes.large,
                        textStyle = MaterialTheme.typography.body2,
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black,
                            focusedIndicatorColor = Pencil700,
                            disabledTextColor = Color.Gray,
                            cursorColor = Color.White,
                            trailingIconColor = Color.White,
                            unfocusedIndicatorColor = Pencil500
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.padding(2.dp),
                        trailingIcon = {Icon(imageVector = Icons.Rounded.Lock, contentDescription = "Password")},
                        shape = Shapes.large,
                        textStyle = MaterialTheme.typography.body2,
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black,
                            focusedIndicatorColor = Pencil700,
                            disabledTextColor = Color.Gray,
                            cursorColor = Color.White,
                            trailingIconColor = Color.White,
                            unfocusedIndicatorColor = Pencil500
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            navController.navigate(HomeRoute)
                        },
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.CenterHorizontally)
                            .size(275.dp, 46.dp)
                        ,
                        shape = Shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Pink500,
                            contentColor = MaterialTheme.colors.onPrimary,
                        )
                    ) {
                        Text("Sign In")
                    }
                    OutlinedButton(
                        onClick = {
                            navController.navigate(RegistrationRoute)
                        },
                        modifier = Modifier
                            .padding(2.dp)
                            .align(Alignment.CenterHorizontally)
                            .size(275.dp, 46.dp)
                        ,
                        shape = Shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Pink500,
                            backgroundColor = Color.Transparent
                        ),
                        border = BorderStroke(2.dp, Pink500)
                    ) {
                        Text("Or create new account", color = Pink500)
                    }
                }
            }
        }
    }
}