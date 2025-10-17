var Module = (function () {
  // State
  let selectedAuthor = null;
  let blueprintsSummary = [];
  let currentBlueprint = null; // {author, name, points: [{x,y}, ...]}
  let createMode = false; // when true, first save will POST

  // Helpers
  const toSummary = (bp) => ({ name: bp.name, points: Array.isArray(bp.points) ? bp.points.length : 0 });

  const renderTable = (list) => {
    const $tbody = $('#blueprints-tbody');
    $tbody.empty();
    list.forEach(({ name, points }) => {
      const $tr = $('<tr>');
      $tr.append($('<td>').text(name));
      $tr.append($('<td>').text(points));
      const $btn = $('<button type="button">').text('Ver plano');
      $btn.on('click', function () { Module.drawBlueprint(selectedAuthor, name); });
      $tr.append($('<td>').append($btn));
      $tbody.append($tr);
    });
  };

  const renderSelectedAuthor = (author) => $('#selected-author').text(author || '');
  const renderSelectedBlueprint = (name) => $('#selected-blueprint').text(name || '');
  const renderTotalPoints = (list) => $('#total-points').text(list.reduce((acc, bp) => acc + bp.points, 0));

  const setAuthor = (author) => { selectedAuthor = author; renderSelectedAuthor(selectedAuthor); };

  const updateBlueprintsByAuthor = (author) => {
    setAuthor(author);
    currentBlueprint = null;
    renderSelectedBlueprint('');
    drawOnCanvas([]);
    if (!author) { blueprintsSummary = []; renderTable(blueprintsSummary); renderTotalPoints(blueprintsSummary); return; }
    // Prefer real API if available, else fallback to mock
    const fetch = (window.apiclient ? window.apiclient.getByAuthor(author) : new Promise((res)=>apimock.getBlueprintsByAuthor(author,res)));
    return fetch.then((blueprints) => {
      // apimock returns array directly; API returns Set -> treat both as arrays
      const list = Array.isArray(blueprints) ? blueprints : Array.from(blueprints || []);
      blueprintsSummary = list.map(toSummary);
      renderTable(blueprintsSummary);
      renderTotalPoints(blueprintsSummary);
    });
  };

  // Canvas drawing
  const drawOnCanvas = (points) => {
    const canvas = document.getElementById('blueprint-canvas');
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    if (!points || points.length === 0) return;
    ctx.beginPath();
    ctx.moveTo(points[0].x, points[0].y);
    for (let i = 1; i < points.length; i++) ctx.lineTo(points[i].x, points[i].y);
    ctx.stroke();
  };

  const drawBlueprint = (author, name) => {
    if (!author || !name) return;
    const fetch = (window.apiclient ? window.apiclient.getOne(author, name) : new Promise((res)=>apimock.getBlueprintsByNameAndAuthor(author, name, res)));
    return fetch.then((bp) => {
      // Some APIs may wrap response; ensure shape
      currentBlueprint = { author: bp.author, name: bp.name, points: (bp.points || []).map(p => ({ x: p.x, y: p.y })) };
      createMode = false;
      renderSelectedBlueprint(currentBlueprint.name);
      drawOnCanvas(currentBlueprint.points);
    });
  };

  // Pointer events
  const getCanvas = () => document.getElementById('blueprint-canvas');
  const canvasPoint = (evt) => {
    const rect = getCanvas().getBoundingClientRect();
    return { x: Math.round(evt.clientX - rect.left), y: Math.round(evt.clientY - rect.top) };
  };
  const onPointerDown = (evt) => {
    if (!currentBlueprint) return; // do nothing if no blueprint selected/created
    const pt = canvasPoint(evt);
    currentBlueprint.points = currentBlueprint.points || [];
    currentBlueprint.points.push(pt);
    drawOnCanvas(currentBlueprint.points);
  };
  const initCanvasHandlers = () => {
    const canvas = getCanvas();
    // Use pointer events; fallback to click if unsupported
    if (window.PointerEvent) {
      canvas.addEventListener('pointerdown', onPointerDown);
    } else {
      canvas.addEventListener('click', (e) => onPointerDown(e));
      canvas.addEventListener('touchstart', (e) => { if (e.touches && e.touches[0]) onPointerDown(e.touches[0]); });
    }
  };

  // Save/Update
  const saveOrUpdate = () => {
    if (!currentBlueprint || !selectedAuthor) return Promise.resolve();
    const bp = { author: selectedAuthor, name: currentBlueprint.name, points: currentBlueprint.points || [] };
    const op = createMode ? window.apiclient.post(bp) : window.apiclient.put(bp);
    return op
      .then(() => window.apiclient.getAll())
      .then((all) => {
        const arr = Array.isArray(all) ? all : Array.from(all || []);
        const byAuthor = arr.filter(b => b.author === selectedAuthor);
        blueprintsSummary = byAuthor.map(toSummary);
        renderTable(blueprintsSummary);
        renderTotalPoints(blueprintsSummary);
        createMode = false; // subsequent saves are PUT
      });
  };

  // Create new blueprint flow
  const createNew = () => {
    if (!selectedAuthor) return;
    const name = prompt('Nombre del nuevo blueprint:');
    if (!name) return;
    currentBlueprint = { author: selectedAuthor, name, points: [] };
    createMode = true;
    renderSelectedBlueprint(name);
    drawOnCanvas([]);
  };

  // Delete flow
  const removeCurrent = () => {
    if (!currentBlueprint || !selectedAuthor || !currentBlueprint.name) return Promise.resolve();
    const { name } = currentBlueprint;
    drawOnCanvas([]);
    currentBlueprint = null;
    renderSelectedBlueprint('');
    return window.apiclient.del(selectedAuthor, name)
      .then(() => window.apiclient.getAll())
      .then((all) => {
        const arr = Array.isArray(all) ? all : Array.from(all || []);
        const byAuthor = arr.filter(b => b.author === selectedAuthor);
        blueprintsSummary = byAuthor.map(toSummary);
        renderTable(blueprintsSummary);
        renderTotalPoints(blueprintsSummary);
      });
  };

  // Public API
  const init = () => { initCanvasHandlers(); };

  return { init, setAuthor, updateBlueprintsByAuthor, drawBlueprint, saveOrUpdate, createNew, removeCurrent };
})();

$(function () {
  Module.init();
  $('#btn-get-blueprints').on('click', function () {
    const author = $('#author').val().trim();
    Module.updateBlueprintsByAuthor(author);
  });
  $('#btn-save').on('click', function () { Module.saveOrUpdate(); });
  $('#btn-create').on('click', function () { Module.createNew(); });
  $('#btn-delete').on('click', function () { Module.removeCurrent(); });
});