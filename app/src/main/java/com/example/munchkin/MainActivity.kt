package com.example.munchkin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.munchkin.model.Jogador
import com.example.munchkin.ui.theme.MunchkinTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LayoutMain()
        }
    }
}

@Composable
fun LayoutMain() {
    val navController = rememberNavController()
    val jogadores = remember { mutableStateListOf<Jogador>() }

    NavHost(navController = navController, startDestination = "jogadores") {
        composable("jogadores") { TelaJogadores(navController = navController, jogadores) }
        composable("cadastro") { CadastroJogador(navController = navController, jogadores) }
        composable("detalhes/{jogadorJSON}") { backStackEntry ->
            val jogadorJSON = backStackEntry.arguments?.getString("jogadorJSON")
            val jogador = Gson().fromJson(jogadorJSON, Jogador::class.java)
            InfoJogador(navController, jogador, jogadores)
        }
    }
}

@Composable
fun TelaJogadores(navController: NavController, jogadores: List<Jogador>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Jogadores", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(15.dp))

        LazyColumn {
            items(jogadores) { jogador ->
                Text(
                    text = "${jogador.nome} - LVL [${jogador.level}] - PODER [${jogador.poder}]",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            val jogadorJSON = Gson().toJson(jogador)
                            navController.navigate("detalhes/$jogadorJSON")
                        }
                )
            }
        }

        Button(
            onClick = { navController.navigate("cadastro") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Adicionar Jogador")
        }
    }
}

@Composable
fun CadastroJogador(navController: NavController, jogadores: MutableList<Jogador>) {
    var nome by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var bonus by remember { mutableStateOf("") }
    var modificadores by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Inserir Novo Jogador", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(50.dp))

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text(text = "Nome do Jogador") },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        TextField(
            value = level,
            onValueChange = { level = it },
            label = { Text(text = "Level") },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        TextField(
            value = bonus,
            onValueChange = { bonus = it },
            label = { Text(text = "Bonus") },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        TextField(
            value = modificadores,
            onValueChange = { modificadores = it },
            label = { Text(text = "Modificadores") },
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            if (nome.isNotEmpty() && level.isNotEmpty() && bonus.isNotEmpty() && modificadores.isNotEmpty()) {
                val levelInt = level.toIntOrNull()
                if (levelInt != null && levelInt < 10) {
                    val jogador = Jogador(nome, levelInt, bonus.toInt(), modificadores.toInt())
                    jogadores.add(jogador)
                    Toast.makeText(context, "Jogador Salvo!", Toast.LENGTH_SHORT).show()
                    nome = ""
                    level = ""
                    bonus = ""
                    modificadores = ""
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "O nível do jogador deve ser menor que 10!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Salvar Jogador")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(text = "Voltar")
        }
    }
}

@Composable
fun InfoJogador(navController: NavController, jogador: Jogador, jogadores: MutableList<Jogador>) {
    var level by remember { mutableStateOf(jogador.level) }
    var bonus by remember { mutableStateOf(jogador.bonus) }
    var modificadores by remember { mutableStateOf(jogador.modificadores) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Estatísticas de '${jogador.nome}'", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(30.dp))

        Text(text = "Nível: $level", fontSize = 20.sp)
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { if (level > 0) level -= 1 }) {
                Text(text = "-")
            }
            Button(onClick = { if (level < 10) level += 1 }) {
                Text(text = "+")
            }
        }
        Spacer(modifier = Modifier.height(15.dp))


        Text(text = "Bônus: $bonus", fontSize = 20.sp)
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { if (bonus > 0) bonus -= 1 }) {
                Text(text = "-")
            }
            Button(onClick = { bonus += 1 }) {
                Text(text = "+")
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Modificadores: $modificadores", fontSize = 20.sp)
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { if (modificadores > 0) modificadores -= 1 }) {
                Text(text = "-")
            }
            Button(onClick = { modificadores += 1 }) {
                Text(text = "+")
            }
        }

        Text("Poder Total: ${level + bonus + modificadores}")

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            jogador.level = level
            jogador.bonus = bonus
            jogador.modificadores = modificadores
            Toast.makeText(navController.context, "Estatísticas Atualizadas!", Toast.LENGTH_SHORT).show()

            val index = jogadores.indexOfFirst { it.nome == jogador.nome }
            if (index != -1) {
                jogadores[index] = jogador
            }

            navController.popBackStack()
        }) {
            Text(text = "Salvar Alterações")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LayoutMain()
}
