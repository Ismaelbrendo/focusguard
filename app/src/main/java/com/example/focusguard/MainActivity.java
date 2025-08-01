package com.example.focusguard;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Configuração da Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Define nossa toolbar como a action bar principal

        // --- Configuração do Navigation Drawer ---
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); // Define que esta classe vai "ouvir" os cliques no menu

        // Este é o "motor" que conecta a toolbar com o drawer layout.
        // Ele cria o ícone "hambúrguer" (☰) e gerencia a animação de abrir/fechar.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // Sincroniza o estado do ícone com o drawer

        // --- Carregar o Fragment Inicial ---
        // Se o app está sendo iniciado pela primeira vez (e não recriado),
        // carregamos o fragment do dashboard como tela inicial.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard); // Deixa o item do menu selecionado
        }
    }

    // --- Lógica para o Clique nos Itens do Menu ---
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Pegamos o ID do item clicado
        int itemId = item.getItemId();

        // Usamos if-else if em vez de switch para evitar o erro de compilação
        if (itemId == R.id.nav_dashboard) {
            // Se clicou em "Dashboard", carrega o DashboardFragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
        } else if (itemId == R.id.nav_blocking) {
            // Se clicou em "Bloquear", carrega o BlockingFragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BlockingFragment()).commit();
        }

        // Fecha o menu lateral após o clique
        drawerLayout.closeDrawer(GravityCompat.START);
        return true; // Retorna 'true' para indicar que o clique foi tratado
    }

    // --- Lógica para o Botão "Voltar" ---
    @Override
    public void onBackPressed() {
        // Se o menu lateral estiver aberto, o botão "voltar" deve apenas fechá-lo.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Se o menu estiver fechado, o botão "voltar" funciona normalmente.
            super.onBackPressed();
        }
    }
}
