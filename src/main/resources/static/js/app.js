var Module = (function () {

  let selectedAuthor = null;
  let blueprintsSummary = [];

  const toSummary = (bp) => ({
    name: bp.name,
    points: Array.isArray(bp.points) ? bp.points.length : 0,
  });

  const renderTable = (list) => {
    const $tbody = $('#blueprints-tbody');
    $tbody.empty();
    list.forEach(({ name, points }) => {
      const $tr = $('<tr>');
      $tr.append($('<td>').text(name));
      $tr.append($('<td>').text(points));
      // Botón para graficar
      const $btn = $('<button type="button">').text('Ver plano');
      $btn.on('click', function () {
        Module.drawBlueprint(selectedAuthor, name);
      });
      $tr.append($('<td>').append($btn));
      $tbody.append($tr);
    });
  };

  const renderSelectedAuthor = (author) => {
    $('#selected-author').text(author || '');
  };

  const renderTotalPoints = (list) => {
    const total = list.reduce((acc, bp) => acc + bp.points, 0);
    $('#total-points').text(total);
  };

  const setAuthor = (author) => {
    selectedAuthor = author;
    renderSelectedAuthor(selectedAuthor);
  };

  const updateBlueprintsByAuthor = (author) => {
    setAuthor(author);
    if (!author) {
      blueprintsSummary = [];
      renderTable(blueprintsSummary);
      renderTotalPoints(blueprintsSummary);
      return;
    }
    apimock.getBlueprintsByAuthor(author, (blueprints) => {
      blueprintsSummary = blueprints.map(toSummary);
      renderTable(blueprintsSummary);
      renderTotalPoints(blueprintsSummary);
    });
  };

  // Dibuja el nombre y los puntos del plano en el canvas
  const renderSelectedBlueprint = (name) => {
    $('#selected-blueprint').text(name || '');
  };

  const drawOnCanvas = (points) => {
    const canvas = document.getElementById('blueprint-canvas');
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    if (!points || points.length === 0) return;
    ctx.beginPath();
    ctx.moveTo(points[0].x, points[0].y);
    for (let i = 1; i < points.length; i++) {
      ctx.lineTo(points[i].x, points[i].y);
    }
    ctx.stroke();
  };

  const drawBlueprint = (author, name) => {
    apimock.getBlueprintsByNameAndAuthor(author, name, (bp) => {
      renderSelectedBlueprint(name);
      drawOnCanvas(bp.points);
    });
  };

  return {
    setAuthor,
    updateBlueprintsByAuthor,
    drawBlueprint
  };
})();

$(function () {
  $('#btn-get-blueprints').on('click', function () {
    const author = $('#author').val().trim();
    Module.updateBlueprintsByAuthor(author);
  });
});