/* Shared GraphQL AJAX client. Treat a GraphQL errors array as a failed request even when HTTP is 200. */
window.Gql = {
  request(query, variables) { return $.ajax({url:'/graphql',method:'POST',contentType:'application/json',data:JSON.stringify({query,variables:variables||{}})}).then(r => { if(r.errors && r.errors.length) return $.Deferred().reject(r.errors.map(e=>e.message).join(' • ')).promise(); return r.data; }); },
  escape(value) { return $('<div>').text(value == null ? '' : String(value)).html(); },
  notice(message, danger) { $('#notice').html(`<div class="alert alert-${danger?'danger':'success'} alert-dismissible fade show" role="alert">${this.escape(message)}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>`); },
  fail(xhr) { this.notice(typeof xhr==='string'?xhr:'Không thể kết nối GraphQL.', true); }
};
