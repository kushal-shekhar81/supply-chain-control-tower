import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

/** App shell: top bar + the routed page. New screens (exceptions, suppliers, what-if) plug in as routes. */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  template: `
    <header class="topbar">
      <h1>Supply Chain Control Tower</h1>
      <span class="topbar__sub">Aurora Mobility · Stratos Aero</span>
    </header>
    <main class="page">
      <router-outlet />
    </main>
  `,
  styleUrl: './app.scss',
})
export class App {}
