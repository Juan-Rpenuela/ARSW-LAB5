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

  return {

    setAuthor,
    updateBlueprintsByAuthor,
  };
})();

$(function () {
  $('#btn-get-blueprints').on('click', function () {
    const author = $('#author').val().trim();
    Module.updateBlueprintsByAuthor(author);
  });
});