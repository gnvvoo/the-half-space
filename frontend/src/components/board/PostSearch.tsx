export function PostSearch({ boardCode, defaultTitle }: { boardCode: string; defaultTitle?: string }) {
  return (
    <form action={`/boards/${boardCode}`} className="flex gap-2">
      <input
        type="text"
        name="title"
        defaultValue={defaultTitle}
        placeholder="제목으로 검색"
        className="w-56 border border-line px-3 py-2 text-sm outline-none focus:border-ink"
      />
      <button
        type="submit"
        className="border border-ink px-3 py-2 text-xs font-semibold text-ink hover:bg-ink hover:text-white"
      >
        검색
      </button>
    </form>
  );
}
