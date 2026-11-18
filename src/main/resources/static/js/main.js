// ============================================================
// NOXAS Service PC — Conexión con el backend
// Fase 1: cotizador por barrio
// ============================================================

const API_BASE = 'http://localhost:8080/api'; // Live Server → back en 8080

// ===== Elementos del DOM =====
const selectBarrio        = document.getElementById('barrio');
const resultadoCotizacion = document.getElementById('resultado-cotizacion');
const textoCotizacion     = document.getElementById('texto-cotizacion');
const restoFormulario     = document.getElementById('resto-formulario');
const mensajeError        = document.getElementById('mensaje-error');
const estadoZona          = document.getElementById('estado-zona');
const estadoZonaTexto     = document.getElementById('estado-zona-texto');
const ctaSolicitar        = document.getElementById('cta-solicitar');

// ===== Estado global =====
let barrioSeleccionado = null; // { barrioNombre, costoEstimado, gratis }

// ============================================================
// 1. Cargar barrios al entrar a la página
// ============================================================
async function cargarBarrios() {
  try {
    const res = await fetch(`${API_BASE}/cobertura/barrios`);
    if (!res.ok) throw new Error('No se pudo cargar la lista de barrios');

    const barrios = await res.json();

    // Limpiar select (dejar solo el placeholder)
    selectBarrio.innerHTML = '<option value="" disabled selected>Elegí tu barrio</option>';

    // Agrupar por anillo de precio según costoVisita
    const anillos = {
      0:    { label: 'Zona base — sin cargo', opts: [] },
      5000: { label: 'Anillo 1 — $5.000',     opts: [] },
      7000: { label: 'Anillo 2 — $7.000',     opts: [] },
      8000: { label: 'Anillo 3 — $8.000',     opts: [] },
    };

    barrios.forEach(b => {
      const precio = Number(b.costoVisita);
      if (anillos[precio]) anillos[precio].opts.push(b);
    });

    // Insertar optgroups en orden
    [0, 5000, 7000, 8000].forEach(precio => {
      const grupo = anillos[precio];
      if (!grupo.opts.length) return;

      const og = document.createElement('optgroup');
      og.label = grupo.label;
      grupo.opts.forEach(b => {
        const opt = document.createElement('option');
        opt.value = b.id;
        opt.textContent = `${b.nombre} (Comuna ${b.comuna})`;
        og.appendChild(opt);
      });
      selectBarrio.appendChild(og);
    });

    // Restaurar barrio guardado (si el usuario ya había elegido antes)
    const guardado = localStorage.getItem('noxas_barrio_id');
    if (guardado && selectBarrio.querySelector(`option[value="${guardado}"]`)) {
      selectBarrio.value = guardado;
      selectBarrio.dispatchEvent(new Event('change'));
    }

  } catch (err) {
    console.error('Error cargando barrios:', err);
    selectBarrio.innerHTML = '<option value="" disabled selected>Error al cargar</option>';
    mostrarError('No pudimos cargar la lista de barrios. Recargá la página.');
  }
}

// ============================================================
// 2. Al cambiar barrio → cotizar
// ============================================================
selectBarrio.addEventListener('change', async () => {
  const barrioId = selectBarrio.value;
  if (!barrioId) return;

  ocultarError();

  // Estado intermedio: consultando
  estadoZonaTexto.textContent = 'Verificando cobertura...';
  estadoZona.querySelector('.material-symbols-outlined').textContent = 'hourglass_top';

  try {
    const res = await fetch(`${API_BASE}/cobertura/${barrioId}`);

    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.mensaje || 'No pudimos cotizar tu barrio');
    }

    const cot = await res.json();
    barrioSeleccionado = cot;

    // Guardar para próximas visitas
    localStorage.setItem('noxas_barrio_id', barrioId);

    // Pintar precio
    if (cot.gratis) {
      textoCotizacion.textContent = `Visita sin cargo en ${cot.barrioNombre}`;
    } else {
      const precio = Number(cot.costoEstimado).toLocaleString('es-AR');
      textoCotizacion.textContent = `${cot.barrioNombre}: $${precio}`;
    }

    // Actualizar cartel de zona
    estadoZona.className = 'p-3 rounded-[4px] bg-tertiary-container/20 border border-tertiary/40 flex items-start gap-2.5';
    estadoZona.querySelector('.material-symbols-outlined').textContent = 'check_circle';
    estadoZona.querySelector('.material-symbols-outlined').classList.remove('text-secondary');
    estadoZona.querySelector('.material-symbols-outlined').classList.add('text-tertiary');
    estadoZona.querySelector('span.font-tech-label-sm').textContent = '¡Cubrimos tu zona!';
    estadoZonaTexto.textContent = `Llegamos a ${cot.barrioNombre}. Podés pedir tu service.`;

    // Mostrar bloques
    resultadoCotizacion.classList.remove('oculto');
    restoFormulario.classList.remove('oculto');
    ctaSolicitar.classList.remove('oculto');

    // Actualizar link de WhatsApp con mensaje personalizado
    actualizarLinkWhatsApp();

  } catch (err) {
    console.error('Error cotizando:', err);
    mostrarError(err.message);

    // Resetear cartel
    estadoZona.className = 'p-3 rounded-[4px] bg-error-container/30 border border-error/40 flex items-start gap-2.5';
    estadoZona.querySelector('.material-symbols-outlined').textContent = 'error';
    estadoZona.querySelector('span.font-tech-label-sm').textContent = 'No pudimos verificar';
    estadoZonaTexto.textContent = err.message;

    resultadoCotizacion.classList.add('oculto');
    restoFormulario.classList.add('oculto');
    ctaSolicitar.classList.add('oculto');
  }
});

// ============================================================
// 3. Actualizar el link de WhatsApp con el barrio elegido
// ============================================================
function actualizarLinkWhatsApp() {
  if (!barrioSeleccionado) return;
  const link = restoFormulario.querySelector('a[href*="wa.me"]');
  if (!link) return;

  const precio = barrioSeleccionado.gratis
    ? 'sin cargo'
    : `$${Number(barrioSeleccionado.costoEstimado).toLocaleString('es-AR')}`;

  const msg = `Hola NOXAS, quiero coordinar un service a domicilio en ${barrioSeleccionado.barrioNombre}. El costo de visita es ${precio}.`;
  link.href = `https://wa.me/5491100000000?text=${encodeURIComponent(msg)}`;
}

// ============================================================
// Helpers
// ============================================================
function mostrarError(msg) {
  mensajeError.textContent = msg;
  mensajeError.classList.remove('oculto');
}
function ocultarError() {
  mensajeError.classList.add('oculto');
  mensajeError.textContent = '';
}

// ============================================================
// Init
// ============================================================
document.addEventListener('DOMContentLoaded', cargarBarrios);