import Link from "next/link";

export function SimpleNavbar() {
  return (
    <header className="border-b border-line">
      <div className="mx-auto flex max-w-[1280px] items-center justify-between px-10 py-5">
        <Link href="/" className="text-xl font-black tracking-tight text-ink">
          THE HALF SPACE
        </Link>
        <Link href="/" className="text-sm font-medium text-ink/70 hover:text-ink">
          홈으로
        </Link>
      </div>
    </header>
  );
}
