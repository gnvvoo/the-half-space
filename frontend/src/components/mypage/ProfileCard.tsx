import type { User } from "@/lib/types";

export function ProfileCard({ user }: { user: User }) {
  return (
    <div className="flex w-[220px] shrink-0 flex-col items-center gap-3 text-center">
      <span className="h-[88px] w-[88px] rounded-full bg-[#E5E5E0]" aria-hidden />
      <span className="text-base font-bold text-ink">{user.nickname}</span>
      <span className="text-xs text-muted">{user.email}</span>
    </div>
  );
}
