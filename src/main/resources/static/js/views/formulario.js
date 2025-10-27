document.addEventListener('DOMContentLoaded', function() {
    const categoriaSelect = document.getElementById('categoria');
    const tipoSelect = document.getElementById('tipo');
    const experienciaInput = document.getElementById('experiencia');
    const rangoInput = document.getElementById('rangoRequerido');
    const recompensaInput = document.getElementById('recompensa');
    const descripcionInput = document.getElementById('descripcion');
    const nombreInput = document.getElementById('nombre');

    // Rangos y sus límites de experiencia
    const rangos = {
        'F': { min: 5, max: 99, base: 100 },
        'E': { min: 100, max: 299, base: 200 },
        'D': { min: 300, max: 599, base: 300 },
        'C': { min: 600, max: 899, base: 500 },
        'B': { min: 900, max: 1199, base: 700 },
        'A': { min: 1200, max: 1499, base: 1000 },
        'S': { min: 1500, max: 2000, base: 1500 }
    };

    // Cuando se selecciona una categoría
    categoriaSelect.addEventListener('change', function() {
        const categoriaId = this.value;
        if (categoriaId) {
            tipoSelect.disabled = false;
            cargarTipos(categoriaId);
        } else {
            tipoSelect.disabled = true;
            tipoSelect.innerHTML = '<option value="">Primero seleccione una categoría</option>';
        }
    });

    // Cuando se selecciona un tipo
    tipoSelect.addEventListener('change', function() {
        const tipoId = this.value;
        if (tipoId) {
            cargarDetallesTipo(tipoId);
        }
    });

    // Cargar tipos por categoría
    function cargarTipos(categoriaId) {
        fetch(`/api/tipos/categoria/${categoriaId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(tipos => {
                if (!Array.isArray(tipos)) {
                    console.warn('La respuesta no es un array:', tipos);
                    tipos = tipos || []; // Si no es array, usar array vacío
                }
                tipoSelect.innerHTML = '<option value="">Seleccione un tipo</option>';
                tipos.forEach(tipo => {
                    if (tipo && tipo.idTipo && tipo.nombreTipo) {
                        const option = document.createElement('option');
                        option.value = tipo.idTipo;
                        option.textContent = tipo.nombreTipo;
                        option.dataset.experiencia = tipo.experienciaAsociada || 0;
                        option.dataset.descripcion = tipo.descripcionBase || '';
                        tipoSelect.appendChild(option);
                    }
                });
                if (tipos.length === 0) {
                    tipoSelect.innerHTML = '<option value="">No hay tipos disponibles</option>';
                }
            })
            .catch(error => {
                console.error('Error al cargar tipos:', error);
                tipoSelect.innerHTML = '<option value="">Error al cargar tipos</option>';
                tipoSelect.disabled = true;
            });
    }

    // Cargar detalles del tipo seleccionado
    function cargarDetallesTipo(tipoId) {
        const tipoOption = tipoSelect.querySelector(`option[value="${tipoId}"]`);
        if (!tipoOption) return;

        const exp = parseInt(tipoOption.dataset.experiencia);
        experienciaInput.value = exp;
        nombreInput.value = tipoOption.textContent;

        // Calcular rango basado en experiencia
        const rango = calcularRango(exp);
        rangoInput.value = rango;

        // Calcular recompensa base
        const recompensaBase = rangos[rango].base;
        const bonusExperiencia = Math.floor(exp * 0.5);
        recompensaInput.value = recompensaBase + bonusExperiencia;

        // Generar descripción
        const descripcionBase = tipoOption.dataset.descripcion;
        const categoriaText = categoriaSelect.options[categoriaSelect.selectedIndex].text;
        descripcionInput.value = generarDescripcion(descripcionBase, {
            tipo: tipoOption.textContent,
            categoria: categoriaText,
            rango: rango
        });
    }

    // Calcular rango basado en experiencia
    function calcularRango(exp) {
        for (let [rango, limites] of Object.entries(rangos)) {
            if (exp >= limites.min && exp <= limites.max) {
                return rango;
            }
        }
        return 'F'; // Rango por defecto
    }

    // Generar descripción dinámica
    function generarDescripcion(base, datos) {
        let descripcion = base
            .replace('{tipo}', datos.tipo)
            .replace('{categoria}', datos.categoria)
            .replace('{rango}', datos.rango);

        // Agregar dificultad basada en rango
        const dificultades = {
            'F': 'fácil',
            'E': 'básica',
            'D': 'moderada',
            'C': 'desafiante',
            'B': 'difícil',
            'A': 'muy difícil',
            'S': 'extremadamente difícil'
        };

        descripcion += `\n\nDificultad: ${dificultades[datos.rango]}`;
        return descripcion;
    }
});